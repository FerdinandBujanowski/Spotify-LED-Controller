package control.song;

import control.type_enums.CurveType;

import java.awt.*;
import java.util.ArrayList;

public interface EventGraphicUnit {

    void syncTracks(TrackTime[] trackTimes);

    void addTrack();
    void removeTrack(int TrackNumber);

    void addEventToTrack(int trackNumber, int msStart, int msDuration, CurveType curveType);
    void deleteEvent(int trackNumber, int oldEventIndex);
    //...

}
