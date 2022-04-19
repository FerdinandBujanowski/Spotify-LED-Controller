package control.song;

import control.type_enums.CurveType;

import java.awt.*;
import java.util.ArrayList;

public interface EventGraphicUnit {

    void syncTracks(TrackTime[] trackTimes);

    void addTrack();
    void removeTrack(int TrackNumber);

    void addEventToTrack(int trackIndex, int msStart, int msDuration, CurveType curveType);
    void deleteEvent(int trackIndex, int oldEventIndex);
    void editEvent(int trackIndex, int msStartOld, int msStartNew, int msDurationNew, CurveType curveTypeNew);
    //...
    void tick(int ms);

    double getEventWidthDivision();
    Point getClosestEventTime(int pixelInPanel);
    void onSelectRequest(int trackIndex, int msStart);
}
