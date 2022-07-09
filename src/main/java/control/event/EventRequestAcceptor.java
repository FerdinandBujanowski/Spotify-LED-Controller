package control.event;

import control.type_enums.CurveType;

import java.awt.*;
import java.util.ArrayList;

public interface EventRequestAcceptor {

    void onSkipTo(int ms);
    void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit);

    Double getTrackIntensityAt(Point coordinates);

    int getCorrespondingEventIndex(int trackNumber, int ms);

    TrackTime[] getTrackTimes();

    int getCurrentSongMs();
    void tick(int ms);

    ArrayList<TimeMeasure> getTimeMeasures();
    TimeMeasure getCorrespondingTimeMeasure(int ms);
    void onAddTimeMeasureRequest(int beatsPerBar, float beatsPerMinute, int msStart, int barsDuration);
    void onSplitTimeMeasureRequest(int msStart, int barsInto);
    void onEditTimeMeasureRequest(int msStartOld, TimeMeasure newTimeMeasureData);

    void onAddTrackRequest();
    void onUpdateTrackRequest(int trackIndex, boolean deleted);

    void onAddEventToTrackRequest(int trackIndex, int msStart, int msDuration, CurveType curveType);
    void onUpdateEventRequest(int trackIndex, int msStartOld, boolean deleted, CurveType curveType, int msStartNew, int msDurationNew);
    Point getUpdatedEventTime(int trackIndex, int eventIndex);
}
