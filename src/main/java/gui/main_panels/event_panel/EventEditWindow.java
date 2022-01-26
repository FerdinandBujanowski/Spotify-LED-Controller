package gui.main_panels.event_panel;

import control.song.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EventEditWindow extends JPanel implements EventGraphicUnit {

    private TrackRequestAcceptor songControl;

    private ArrayList<JLabel> trackLabels;
    private ArrayList<ArrayList<JLabel>> eventLabels;

    private double EVENT_WIDTH_DIVISION = 20;

    public EventEditWindow(TrackRequestAcceptor songControl) {
        super(null);

        this.songControl = songControl;
        this.songControl.setEventGraphicUnit(this);

        this.trackLabels = new ArrayList<>();
        this.eventLabels = new ArrayList<>();

        this.setOpaque(true);
        this.setBackground(new Color(218, 218, 218));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ArrayList<TimeMeasure> timeMeasures = this.songControl.getTimeMeasures();
        if(timeMeasures != null) {
            for(TimeMeasure timeMeasure : timeMeasures) {
                double msSingleBeat = 1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0);
                for(int i = 0; i < timeMeasure.getBeatsDuration(); i++) {

                    if(i % timeMeasure.getBeatsPerBar() == 0) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.GRAY);
                    }
                    int x = (int)Math.round((timeMeasure.getMsStart() / this.EVENT_WIDTH_DIVISION)
                        + (i * (msSingleBeat / this.EVENT_WIDTH_DIVISION)));
                    g.drawLine(x, 0, x, this.getHeight());
                }
            }
        }

        TrackTime[] trackTimes = this.songControl.getTrackTimes();
        if(trackTimes != null) {
            for(int i = 0; i < trackTimes.length; i++) {
                Point[] points = trackTimes[i].getPoints();
                for(int j = 0; j < points.length; j++) {
                    g.setColor(Color.WHITE);
                    g.fillRect(
                            points[j].x / (int)this.EVENT_WIDTH_DIVISION,
                            20 + (i * 50),
                            points[j].y / (int)this.EVENT_WIDTH_DIVISION,
                            30
                    );
                    g.setColor(Color.BLACK);
                    g.drawRect(
                            points[j].x / (int)this.EVENT_WIDTH_DIVISION,
                            20 + (i * 50),
                            points[j].y / (int)this.EVENT_WIDTH_DIVISION,
                            30
                    );
                }
            }
        }
    }

    @Override
    public void syncTracks(TrackTime[] trackTimes) {

        int totalWidth = 0;
        for(TimeMeasure timeMeasure : this.songControl.getTimeMeasures()) {
            int msSingleBeat = (int)Math.round(1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0));
            totalWidth += msSingleBeat * timeMeasure.getBeatsDuration();
        }
        this.setPreferredSize(new Dimension(totalWidth / (int)this.EVENT_WIDTH_DIVISION, this.getPreferredSize().height));

        for(int i = 0; i < trackTimes.length; i++) {
            this.addTrack();
            for(Point point : trackTimes[i].getPoints()) {
                this.addEventToTrack(i, point.x, point.y);
            }
        }

        this.repaint();
    }

    @Override
    public void addTrack() {

       JLabel trackLabel = new JLabel();
       trackLabel.setOpaque(true);
       trackLabel.setBackground(new Color(154, 154, 154));

       trackLabel.setBounds(
               0,
               20 + (this.trackLabels.size() * 50),
               this.getPreferredSize().width,
               30
       );

       trackLabel.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               //TODO: user input triggers requests to SongControl
               super.mouseClicked(e);
           }
       });

       this.trackLabels.add(trackLabel);
       //this.add(trackLabel);

       this.eventLabels.add(new ArrayList<>());
    }

    @Override
    public void removeTrack(int trackNumber) {
        this.trackLabels.remove(trackNumber);
    }

    @Override
    public void addEventToTrack(int trackNumber, int msStart, int msDuration) {
        JLabel eventLabel = new JLabel();
        eventLabel.setOpaque(true);
        eventLabel.setBackground(Color.BLUE);

        eventLabel.setBounds(
                msStart / (int)this.EVENT_WIDTH_DIVISION + 2,
                20 + (trackNumber * 50),
                msDuration / (int)this.EVENT_WIDTH_DIVISION - 2,
                30
        );

        eventLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO: user input triggers request to SongControl
                super.mouseClicked(e);

                System.out.println("Event clicked [track "
                        + trackNumber + " - milliseconds " + msStart + " to " + (msStart + msDuration) + "]");
            }
        });

        this.eventLabels.get(trackNumber).add(eventLabel);
        this.add(eventLabel);
    }
}
