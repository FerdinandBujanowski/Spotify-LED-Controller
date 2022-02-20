package control.song;

import control.type_enums.CurveType;

import java.awt.*;
import java.util.ArrayList;

public interface TrackRequestAcceptor {

    void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit);

    Double getTrackIntensityAt(Point coordinates);

    ArrayList<TimeMeasure> getTimeMeasures();
    TimeMeasure getCorrespondingTimeMeasure(int ms);
    int getCorrespondingEventIndex(int trackNumber, int ms);

    TrackTime[] getTrackTimes();

    int getCurrentSongMs();
    void tick(int ms);

    void onAddTrackRequest();
    void onUpdateTrackRequest(int trackNumber, boolean deleted);

    void onAddEventToTrackRequest(int trackNumber, int msStart, int msDuration);
    void onUpdateEventRequest(int trackNumber, int msStartOld, boolean deleted, CurveType curveType, int msStartNew, int msDurationNew);

}
