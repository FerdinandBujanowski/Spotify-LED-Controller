package control.event;

import control.save.EventSaveUnit;
import control.type_enums.CurveType;
import logic.event.LogicEvent;
import logic.event.LogicTrack;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class EventControl implements EventRequestAcceptor, Serializable {

    private EventGraphicUnit eventWindow;
    private ArrayList<LogicTrack> logicTracks;
    private ArrayList<TimeMeasure> timeMeasures;

    private int currentSongMs;

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
        return this.logicTracks.size();
    }

    public double[] getTrackIntensitiesAt(int ms) {
        double[] values = new double[this.logicTracks.size()];
        for(int i = 0; i < values.length; i++) {
            values[i] = this.getTrackIntensityAt(new Point(i, ms));
        }
        return values;
    }

    @Override
    public void onSkipTo(int ms) {

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
    public void onAddEventToTrackRequest(int trackIndex, int msStart, int msDuration, CurveType curveType) {
        if(trackIndex == 0) {
            //TODO: adding a time measure
        }
        else if(this.logicTracks.get(trackIndex) != null) {
            int oldLength = this.logicTracks.get(trackIndex).getEventsCopyArray().length;
            this.logicTracks.get(trackIndex).addEventToTrack(msStart, msDuration, curveType, -1);

            //TODO: Prüfen nach Overlaps klappt anscheinend noch nicht
            LogicEvent[] events = this.logicTracks.get(trackIndex).getEventsCopyArray();
            if(events.length > oldLength) {
                LogicEvent newEvent = events[oldLength];
                this.eventWindow.addEventToTrack(trackIndex, newEvent.getMsStart(), newEvent.getMsDuration(), newEvent.getCurveType());
            }
        }
    }

    @Override
    public void onUpdateEventRequest(int trackIndex, int msStartOld, boolean deleted, CurveType curveType, int msStartNew, int msDurationNew) {
        if(trackIndex == 0) {

        }
        int eventIndex = this.logicTracks.get(trackIndex).getEventIndex(msStartOld);
        if(eventIndex == -1) return;

        this.logicTracks.get(trackIndex).removeEventAtIndex(eventIndex);
        if(deleted) {
            this.eventWindow.deleteEvent(trackIndex, eventIndex);
        }
        else {
            this.logicTracks.get(trackIndex).addEventToTrack(msStartNew, msDurationNew, curveType, eventIndex);
            Point updatedEventTime = this.getUpdatedEventTime(trackIndex, eventIndex);
            this.eventWindow.editEvent(trackIndex, msStartOld, updatedEventTime.x, updatedEventTime.y, curveType);
        }
    }

    @Override
    public Point getUpdatedEventTime(int trackIndex, int eventIndex) {
        return this.logicTracks.get(trackIndex).getEventTime(eventIndex);
    }

    public EventSaveUnit createEventSaveUnit() {
        return new EventSaveUnit(this.logicTracks, this.timeMeasures);
    }
}
