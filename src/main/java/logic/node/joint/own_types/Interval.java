package logic.node.joint.own_types;

import java.io.Serializable;

public class Interval implements Serializable {

    private final int start, end;

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Interval(int end) {
        this(0, end);
    }

    public int getStart() {
        return this.start;
    }
    public int getEnd() {
        return this.end;
    }
    public int getLength() {
        return this.end - this.start;
    }

    @Override
    public String toString() {
        return "Interval {" +
                "start = " + start +
                ", end = " + end +
                '}';
    }
}
