package gui.main_panels.event_panel;

import control.song.SongControl;
import control.song.TimeMeasure;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EventEditWindow extends JPanel {

    private SongControl songControl;

    public EventEditWindow(SongControl songControl) {
        super(null);

        this.songControl = songControl;
        this.setOpaque(true);
        this.setBackground(new Color(111, 159, 152));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.setPreferredSize(new Dimension(5000, this.getHeight()));
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
                    int x = i * ((timeMeasure.getMsStart() + msSingleBeat) / 10);
                    g.drawLine(x, 0, x, this.getHeight());
                }
            }
        }
    }
}
