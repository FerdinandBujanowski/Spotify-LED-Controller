package control.song;

public class TimeMeasure {

    private int beatsPerBar;
    private float beatsPerMinute;

    private int msStart;
    private int beatsDuration;

    private int lengthOneBeat;
    private int lengthOneBar;

    public TimeMeasure(int beatsPerBar, float beatsPerMinute, int msStart, int beatsDuration) {
        this.beatsPerBar = beatsPerBar;
        this.beatsPerMinute = beatsPerMinute;
        this.msStart = msStart;
        this.beatsDuration = beatsDuration;

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
    public int getBeatsDuration() {
        return this.beatsDuration;
    }

    public int getLengthOneBeat() {
        return this.lengthOneBeat;
    }
    public int getLengthOneBar() {
        return this.lengthOneBar;
    }
}
