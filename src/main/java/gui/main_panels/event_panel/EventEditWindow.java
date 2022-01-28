package gui.main_panels.event_panel;

import control.song.*;
import control.type_enums.TimeSignature;

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
    private TimeSignature barRoster;

    public EventEditWindow(TrackRequestAcceptor songControl) {
        super(null);

        this.songControl = songControl;
        this.songControl.setEventGraphicUnit(this);

        this.trackLabels = new ArrayList<>();
        this.eventLabels = new ArrayList<>();

        this.barRoster = TimeSignature.ONE_FOUR;

        this.setOpaque(true);
        this.setBackground(new Color(218, 218, 218));
    }

    public TimeSignature getBarRoster() {
        return this.barRoster;
    }
    public void setBarRoster(TimeSignature barRoster) {
        this.barRoster = barRoster;
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ArrayList<TimeMeasure> timeMeasures = this.songControl.getTimeMeasures();
        if(timeMeasures != null) {
            for(TimeMeasure timeMeasure : timeMeasures) {

                double msSingleBeat = 1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0);
                double msFullBar = msSingleBeat * timeMeasure.getBeatsPerBar();

                g.setColor(Color.GRAY);
                for(int i = 0; i < (int)Math.round(timeMeasure.getBeatsDuration() * (1.0 / this.barRoster.getRatio())); i++) {
                    int x = (int)Math.round((timeMeasure.getMsStart() / this.EVENT_WIDTH_DIVISION)
                    + ((this.barRoster.getRatio() * (double)i) * (msFullBar / this.EVENT_WIDTH_DIVISION)));
                    g.drawLine(x, 0, x, this.getHeight());
                }

                g.setColor(Color.BLACK);
                for(int i = 0; i < timeMeasure.getBeatsDuration(); i += timeMeasure.getBeatsPerBar()) {
                    int x = (int)Math.round((timeMeasure.getMsStart() / this.EVENT_WIDTH_DIVISION)
                            + ((double)i * (msSingleBeat / this.EVENT_WIDTH_DIVISION)));
                    g.drawLine(x, 0, x, this.getHeight());
                }
            }
        }
    }

    @Override
    public void syncTracks(TrackTime[] trackTimes) {

        int totalWidth = 0;
        for(TimeMeasure timeMeasure : this.songControl.getTimeMeasures()) {
            int msSingleBeat = timeMeasure.getLengthOneBeat();
            totalWidth += (msSingleBeat * timeMeasure.getBeatsDuration());
        }
        this.setPreferredSize(new Dimension(
                totalWidth / (int)this.EVENT_WIDTH_DIVISION,
                this.getPreferredSize().height));

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
       trackLabel.setBackground(new Color(238, 238, 238));

       trackLabel.setBounds(
               0,
               20 + (this.trackLabels.size() * 50),
               this.getPreferredSize().width,
               30
       );

       int currentIndex = this.trackLabels.size();

       trackLabel.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               //TODO: user input triggers requests to SongControl
               if(e.getClickCount() == 2) {
                   Point clickLocation = e.getPoint();

                   int msInSong = clickLocation.x * (int)EVENT_WIDTH_DIVISION;
                   TimeMeasure timeMeasure = songControl.getCorrespondingTimeMeasure(msInSong);
                   double msOneSection = timeMeasure.getLengthOneBar() * barRoster.getRatio();
                   int msInTimeMeasure = msInSong - timeMeasure.getMsStart();

                   songControl.onAddEventToTrackRequest(
                           currentIndex,
                           (msInSong) - (int)Math.round(msInTimeMeasure % msOneSection),
                           (int)Math.round(msOneSection)
                   );

               }
           }
       });

       this.trackLabels.add(trackLabel);
       this.add(trackLabel);

       this.eventLabels.add(new ArrayList<>());
       this.repaint();
    }

    @Override
    public void removeTrack(int trackNumber) {
        this.trackLabels.remove(trackNumber);
    }

    @Override
    public void addEventToTrack(int trackNumber, int msStart, int msDuration) {
        JLabel eventLabel = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, this.getSize().width, this.getSize().height);
            }
        };
        eventLabel.setOpaque(true);
        eventLabel.setBackground(new Color(0, 11, 147));

        eventLabel.setBounds(
                msStart / (int)this.EVENT_WIDTH_DIVISION,
                20 + (trackNumber * 50),
                msDuration / (int)this.EVENT_WIDTH_DIVISION,
                30
        );

        eventLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO: user input triggers request to SongControl

                System.out.println("Event clicked [track "
                        + trackNumber + " - milliseconds " + msStart + " to " + (msStart + msDuration) + "]");
            }
        });

        this.eventLabels.get(trackNumber).add(eventLabel);
        this.add(eventLabel);
        eventLabel.getParent().setComponentZOrder(eventLabel, 0);
        this.repaint();
    }
}
