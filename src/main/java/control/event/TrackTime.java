package control.event;

import control.type_enums.CurveType;
import logic.event.LogicEvent;

import java.awt.*;
import java.io.Serializable;

public class TrackTime implements Serializable {

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
