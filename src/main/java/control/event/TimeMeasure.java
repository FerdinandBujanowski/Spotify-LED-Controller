package control.event;

import java.io.Serializable;

public class TimeMeasure implements Serializable {

    private final int beatsPerBar;
    private final float beatsPerMinute;

    private final int msStart;
    private final int barsDuration;

    private final int lengthOneBeat;
    private final int lengthOneBar;

    public TimeMeasure(int beatsPerBar, float beatsPerMinute, int msStart, int barsDuration) {
        this.beatsPerBar = beatsPerBar;
        this.beatsPerMinute = beatsPerMinute;
        this.msStart = msStart;
        this.barsDuration = barsDuration;

        this.lengthOneBeat = (int)Math.round(1000.0 / (this.beatsPerMinute / 60.0));
        this.lengthOneBar = this.lengthOneBeat * this.beatsPerBar;
    }

    public int getBeatsPerBar() {
        return this.beatsPerBar;
    }
    public float getBeatsPerMinute() {
        return this.beatsPerMinute;
    }
    public int getMsStart() {
        return this.msStart;
    }
    public int getBarsDuration() {
        return this.barsDuration;
    }

    public int getLengthOneBeat() {
        return this.lengthOneBeat;
    }
    public int getLengthOneBar() {
        return this.lengthOneBar;
    }
}
