package control.song;

import logic.song.LogicEvent;

import java.awt.*;
import java.util.ArrayList;

public class TrackTime {

    private Point[] points;

    public TrackTime(LogicEvent[] events) {
        this.points = new Point[events.length];

        for(int i = 0; i < events.length; i++) {
            this.points[i] = new Point(events[i].getMsStart(), events[i].getMsDuration());
        }
    }

    public Point[] getPoints() {
        return this.points;
    }
}
