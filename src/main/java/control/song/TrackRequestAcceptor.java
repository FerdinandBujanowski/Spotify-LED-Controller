package control.song;

import java.util.ArrayList;

public interface TrackRequestAcceptor {

    void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit);

    ArrayList<TimeMeasure> getTimeMeasures();
    TrackTime[] getTrackTimes();

    void onAddTrackRequest();
    void onUpdateTrackRequest(int trackNumber, boolean deleted);

    void onUpdateEventRequest(int trackNumber, int msStartOld, boolean deleted, int msStartNew, int msDurationNew);

}
