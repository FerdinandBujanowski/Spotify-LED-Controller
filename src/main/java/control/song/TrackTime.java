package control.song;

import control.type_enums.CurveType;
import logic.song.LogicEvent;

import java.awt.*;
import java.util.ArrayList;

public class TrackTime {

    private Point[] points;
    private CurveType[] curveTypes;

    public TrackTime(LogicEvent[] events, CurveType[] curveTypes) {
        this.points = new Point[events.length];
        this.curveTypes = curveTypes;

        for(int i = 0; i < events.length; i++) {
            this.points[i] = new Point(events[i].getMsStart(), events[i].getMsDuration());
        }
    }

    public Point[] getPoints() {
        return this.points;
    }
    public CurveType[] getCurveTypes() {
        return this.curveTypes;
    }
}
