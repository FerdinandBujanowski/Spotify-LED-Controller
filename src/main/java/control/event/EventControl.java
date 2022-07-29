package control.event;

import control.save.EventSaveUnit;
import control.song.SongEventCommunication;
import control.type_enums.CurveType;
import logic.event.LogicEvent;
import logic.event.LogicTrack;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class EventControl implements EventRequestAcceptor, EventSongCommunication, Serializable {

    private EventGraphicUnit eventWindow;
    private ArrayList<LogicTrack> logicTracks;
    private ArrayList<TimeMeasure> timeMeasures;

    private int currentSongMs;

    private SongEventCommunication songEventCommunication;

    public EventControl() {

        this.eventWindow = null;
        this.timeMeasures = new ArrayList<>();
        this.logicTracks = new ArrayList<>();

        this.currentSongMs = 0;
    }

    public void reinitialize(EventSaveUnit eventSaveUnit) {
        this.logicTracks = eventSaveUnit.getLogicTracks();
        this.timeMeasures = eventSaveUnit.getTimeMeasures();
        this.eventWindow.syncTracks(this.getTrackTimes());
    }

    public int getTrackCount() {
        return this.logicTracks.size() - 1;
    }

    public double[] getTrackIntensitiesAt(int ms) {
        double[] values = new double[this.logicTracks.size() - 1];
        for(int i = 0; i < values.length; i++) {
            values[i] = this.getTrackIntensityAt(new Point(i + 1, ms));
        }
        return values;
    }

    public void setSongEventCommunication(SongEventCommunication songEventCommunication) {
        this.songEventCommunication = songEventCommunication;
    }

    @Override
    public void onSkipTo(int ms) {
        this.songEventCommunication.onSkipTo(ms);
        this.tick(ms);
    }

    @Override
    public void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit) {
        this.eventWindow = eventGraphicUnit;
    }

    @Override
    public Double getTrackIntensityAt(Point coordinates) {
        if(this.logicTracks.get(coordinates.x) == null) {
            return 0.d;
        } else {
            return this.logicTracks.get(coordinates.x).getIntensityAt(coordinates.y);
        }
    }

    @Override
    public int getCorrespondingEventIndex(int trackIndex, int ms) {
        return this.logicTracks.get(trackIndex).getEventIndex(ms);
    }

    @Override
    public TrackTime[] getTrackTimes() {

        TrackTime[] trackTimes = new TrackTime[this.logicTracks.size()];

        for(int i = 0; i < this.logicTracks.size(); i++) {
            trackTimes[i] = new TrackTime(this.logicTracks.get(i).getEventsCopyArray(), this.logicTracks.get(i).getCurveTypes());
        }
        return trackTimes;
    }

    @Override
    public int getCurrentSongMs() {
        return this.currentSongMs;
    }

    @Override
    public void tick(int ms) {
        eventWindow.tick(ms);
    }

    @Override
    public ArrayList<TimeMeasure> getTimeMeasures() {
        return this.timeMeasures;
    }

    @Override
    public TimeMeasure getCorrespondingTimeMeasure(int ms) {
        for(TimeMeasure timeMeasure : this.timeMeasures) {
            int msEnd = timeMeasure.getMsStart() + (timeMeasure.getLengthOneBeat() * timeMeasure.getBarsDuration() * timeMeasure.getBeatsPerBar());
            if(ms >= timeMeasure.getMsStart() && ms < msEnd) {
                return timeMeasure;
            }
        }
        return null;
    }

    @Override
    public void onAddTimeMeasureRequest(int beatsPerBar, float beatsPerMinute, int msStart, int barsDuration) {
        TimeMeasure newTimeMeasure = new TimeMeasure(beatsPerBar, beatsPerMinute, msStart, barsDuration);
        this.timeMeasures.add(newTimeMeasure);
        this.eventWindow.addTimeMeasure(newTimeMeasure);
    }

    @Override
    public void onSplitTimeMeasureRequest(int msStart, int barsInto) {
        this.eventWindow.removeTimeMeasure(msStart);

        for(TimeMeasure timeMeasure : this.timeMeasures) {
            if(timeMeasure.getMsStart() == msStart) {
                this.timeMeasures.remove(timeMeasure);
                int barsRight = timeMeasure.getBarsDuration() - barsInto;
                int msInto = msStart + (timeMeasure.getLengthOneBar() * barsInto);

                this.onAddTimeMeasureRequest(
                        timeMeasure.getBeatsPerBar(),
                        timeMeasure.getBeatsPerMinute(),
                        msStart,
                        barsInto
                );
                this.onAddTimeMeasureRequest(
                        timeMeasure.getBeatsPerBar(),
                        timeMeasure.getBeatsPerMinute(),
                        msInto,
                        barsRight
                );
                return;
            }
        }

    }

    @Override
    public void onEditTimeMeasureRequest(int msStartOld, TimeMeasure newTimeMeasureData) {

    }

    @Override
    public void onAddTrackRequest() {
        //TODO: hier alle möglichen Fehler abfangen
        //this.eventWindow.updateBounds();
        this.logicTracks.add(new LogicTrack());
        this.eventWindow.addTrack();
    }
    @Override
    public void onUpdateTrackRequest(int trackIndex, boolean deleted) {

    }

    @Override
    public void onAddEventToTrackRequest(int trackIndex, int msStart, int msDuration, CurveType curveType, double userInput) {
        if(trackIndex == 0) {
            return;
            //TODO: adding a time measure
        }
        if(this.logicTracks.get(trackIndex) != null) {
            int oldLength = this.logicTracks.get(trackIndex).getEventsCopyArray().length;
            this.logicTracks.get(trackIndex).addEventToTrack(msStart, msDuration, curveType, -1, userInput);

            LogicEvent[] events = this.logicTracks.get(trackIndex).getEventsCopyArray();
            if(events.length > oldLength) {
                LogicEvent newEvent = events[oldLength];
                this.eventWindow.addEventToTrack(trackIndex, newEvent.getMsStart(), newEvent.getMsDuration(), newEvent.getCurveType(), userInput);
            }
        }
    }

    @Override
    public ArrayList<Point> onCopyEventsRequest(ArrayList<Point> indexes) {
        ArrayList<Point> newIndexes = new ArrayList<>();
        for(Point index : indexes) {
            Point eventTime = this.logicTracks.get(index.x).getEventTime(index.y);
            CurveType curveType = this.logicTracks.get(index.x).getCurveTypeAt(index.y);
            double userInput = this.logicTracks.get(index.x).getUserInputAt(index.y);

            Point newIndex = new Point(index.x, this.logicTracks.get(index.x).getCurrentEventCount());
            this.onAddEventToTrackRequest(newIndex.x, eventTime.x, eventTime.y, curveType, userInput);

            newIndexes.add(newIndex);
        }
        return newIndexes;
    }

    @Override
    public void onUpdateEventRequest(int trackIndex, int eventIndex, int msStartOld, boolean deleted, CurveType curveType, double userInput, int msStartNew, int msDurationNew) {
        if(trackIndex == 0) {
            return;
        }
        this.logicTracks.get(trackIndex).removeEventAtIndex(eventIndex);
        if(deleted) {
            this.eventWindow.deleteEvent(trackIndex, eventIndex);
        }
        else {
            this.logicTracks.get(trackIndex).addEventToTrack(msStartNew, msDurationNew, curveType, eventIndex, userInput);
            Point updatedEventTime = this.getUpdatedEventTime(trackIndex, eventIndex);
            this.eventWindow.editEvent(trackIndex, eventIndex, updatedEventTime.x, updatedEventTime.y, curveType, userInput);
        }
    }

    @Override
    public Point getUpdatedEventTime(int trackIndex, int eventIndex) {
        return this.logicTracks.get(trackIndex).getEventTime(eventIndex);
    }

    public EventSaveUnit createEventSaveUnit() {
        return new EventSaveUnit(this.logicTracks, this.timeMeasures);
    }

    @Override
    public void importTimeMeasure(TimeMeasure timeMeasure) {
        while(!this.timeMeasures.isEmpty()) {
            this.eventWindow.removeTimeMeasure(this.timeMeasures.get(0).getMsStart());
            this.timeMeasures.remove(0);
        }
        this.timeMeasures.add(timeMeasure);
        this.eventWindow.addTimeMeasure(timeMeasure);
    }
}
