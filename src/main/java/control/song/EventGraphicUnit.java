package control.song;

import java.util.ArrayList;

public interface EventGraphicUnit {

    void syncTracks(TrackTime[] trackTimes);

    void addTrack();
    void removeTrack(int TrackNumber);

    void addEventToTrack(int TrackNumber, int msStart, int msDuration);
    //...

}
