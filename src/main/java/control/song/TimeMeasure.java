package control.song;

public class TimeMeasure {

    private int beatsPerBar;
    private float beatsPerMinute;

    private int msStart;
    private int beatsDuration;

    public TimeMeasure(int beatsPerBar, float beatsPerMinute, int msStart, int beatsDuration) {
        this.beatsPerBar = beatsPerBar;
        this.beatsPerMinute = beatsPerMinute;
        this.msStart = msStart;
        this.beatsDuration = beatsDuration;
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
}
