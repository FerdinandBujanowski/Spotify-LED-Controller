package control.save;

import control.song.TimeMeasure;
import logic.song.LogicTrack;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;

public class EventSaveUnit implements Serializable {

    private final String selectedSongId;
    private final ImageIcon albumImage;
    private final ArrayList<LogicTrack> logicTracks;
    private final ArrayList<TimeMeasure> timeMeasures;

    public EventSaveUnit(String selectedSongId, ImageIcon albumImage, ArrayList<LogicTrack> logicTracks, ArrayList<TimeMeasure> timeMeasures) {
        this.selectedSongId = selectedSongId;
        this.albumImage = albumImage;
        this.logicTracks = logicTracks;
        this.timeMeasures = timeMeasures;
    }

    public String getSelectedSongId() {
        return this.selectedSongId;
    }
    public ImageIcon getAlbumImage() {
        return this.albumImage;
    }
    public ArrayList<LogicTrack> getLogicTracks() {
        return this.logicTracks;
    }

    public ArrayList<TimeMeasure> getTimeMeasures() {
        return this.timeMeasures;
    }
}
