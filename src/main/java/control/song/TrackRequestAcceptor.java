package control.song;

import java.util.ArrayList;

public interface TrackRequestAcceptor {

    void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit);

    ArrayList<TimeMeasure> getTimeMeasures();
    TrackTime[] getTrackTimes();

    void onUpdateTrackRequest(String trackName, boolean deleted);

    void onUpdateEventRequest(String trackName, int msStartOld, boolean deleted, int msStartNew, int msDurationNew);
}
