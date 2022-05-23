package control.song;

import control.type_enums.CurveType;

import java.awt.*;
import java.util.ArrayList;

public interface TrackRequestAcceptor {

    void onSkipTo(int ms);
    void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit);

    Double getTrackIntensityAt(Point coordinates);

    ArrayList<TimeMeasure> getTimeMeasures();
    TimeMeasure getCorrespondingTimeMeasure(int ms);
    int getCorrespondingEventIndex(int trackNumber, int ms);

    TrackTime[] getTrackTimes();

    int getCurrentSongMs();
    void tick(int ms);

    void onAddTrackRequest();
    void onUpdateTrackRequest(int trackIndex, boolean deleted);

    void onAddEventToTrackRequest(int trackIndex, int msStart, int msDuration, CurveType curveType);
    void onUpdateEventRequest(int trackIndex, int msStartOld, boolean deleted, CurveType curveType, int msStartNew, int msDurationNew);
    Point getUpdatedEventTime(int trackIndex, int eventIndex);
}
