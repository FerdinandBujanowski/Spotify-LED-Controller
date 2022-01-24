package gui.main_panels.event_panel;

import control.song.SongControl;
import control.song.TimeMeasure;
import control.song.TrackTime;
import control.song.Updatable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EventEditWindow extends JPanel implements Updatable {

    private SongControl songControl;

    private int EVENT_WIDTH_DIVISION = 20;

    public EventEditWindow(SongControl songControl) {
        super(null);

        this.songControl = songControl;
        this.songControl.setUpdatable(this);

        this.setOpaque(true);
        this.setBackground(new Color(111, 159, 152));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ArrayList<TimeMeasure> timeMeasures = this.songControl.getTimeMeasures();
        if(timeMeasures != null) {
            for(TimeMeasure timeMeasure : timeMeasures) {
                int msSingleBeat = (int)Math.round(1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0));
                for(int i = 0; i < timeMeasure.getBeatsDuration(); i++) {

                    if(i % timeMeasure.getBeatsPerBar() == 0) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.GRAY);
                    }
                    int x = i * ((timeMeasure.getMsStart() + msSingleBeat) / this.EVENT_WIDTH_DIVISION);
                    g.drawLine(x, 0, x, this.getHeight());
                }
            }
        }

        TrackTime[] trackTimes = this.songControl.getTrackTimes();
        if(trackTimes != null) {
            for(int i = 0; i < trackTimes.length; i++) {
                Point[] points = trackTimes[i].getPoints();
                for(int j = 0; j < points.length; j++) {
                    g.setColor(Color.BLUE);
                    g.fillRect(
                            points[j].x / this.EVENT_WIDTH_DIVISION,
                            20 + (i * 50),
                            points[j].y / this.EVENT_WIDTH_DIVISION,
                            30
                    );
                    g.setColor(Color.BLACK);
                    g.drawRect(
                            points[j].x / this.EVENT_WIDTH_DIVISION,
                            20 + (i * 50),
                            points[j].y / this.EVENT_WIDTH_DIVISION,
                            30
                    );
                }
            }
        }
    }

    @Override
    public void update() {
        int totalWidth = 0;
        for(TimeMeasure timeMeasure : this.songControl.getTimeMeasures()) {
            int msSingleBeat = (int)Math.round(1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0));
            totalWidth += msSingleBeat * timeMeasure.getBeatsDuration();
        }
        this.setPreferredSize(new Dimension(totalWidth / this.EVENT_WIDTH_DIVISION, this.getPreferredSize().height));
        this.repaint();
    }
}
