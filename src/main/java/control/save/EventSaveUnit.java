package control.save;

import control.event.TimeMeasure;
import logic.event.LogicTrack;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;

public class EventSaveUnit implements Serializable {

    private final ArrayList<LogicTrack> logicTracks;
    private final ArrayList<TimeMeasure> timeMeasures;

    public EventSaveUnit(ArrayList<LogicTrack> logicTracks, ArrayList<TimeMeasure> timeMeasures) {
        this.logicTracks = logicTracks;
        this.timeMeasures = timeMeasures;
    }

    public ArrayList<LogicTrack> getLogicTracks() {
        return this.logicTracks;
    }

    public ArrayList<TimeMeasure> getTimeMeasures() {
        return this.timeMeasures;
    }
}
