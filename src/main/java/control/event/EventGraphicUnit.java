package control.event;

import control.type_enums.CurveType;

import java.awt.*;

public interface EventGraphicUnit {

    void syncTracks(TrackTime[] trackTimes);
    void updateBounds();

    void addTimeMeasure(TimeMeasure timeMeasure);
    void removeTimeMeasure(int msStart);

    void addTrack();
    void removeTrack(int TrackNumber);

    void addEventToTrack(int trackIndex, int msStart, int msDuration, CurveType curveType);
    void deleteEvent(int trackIndex, int oldEventIndex);
    void editEvent(int trackIndex, int eventIndex, int msStartNew, int msDurationNew, CurveType curveTypeNew);
    //...
    void tick(int ms);

    double getEventWidthDivision();
    Point getClosestEventTime(int pixelInPanel);

    CurveType getDefaultCurveType();
    void setDefaultCurveType(CurveType curveType);

    boolean getCurveBrush();
    void setCurveBrush(boolean curveBrush);

    void onSelectionEvent(int trackIndex, int eventIndex);
}
