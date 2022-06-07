package gui.main_panels.event_panel;

import control.song.*;
import control.type_enums.CurveType;
import control.type_enums.TimeSignature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EventEditWindow extends JPanel implements EventGraphicUnit {

    private TrackRequestAcceptor songControl;

    private ArrayList<JLabel> trackLabels;
    private ArrayList<ArrayList<GraphicEvent>> graphicEvents;
    private JLabel cursorLabel;

    private double eventWidthDivision = 20;
    private TimeSignature barRoster;

    private boolean toggleCtrl;

    private CurveType defaultCurveType;
    private boolean curveBrush;

    public EventEditWindow(TrackRequestAcceptor songControl) {
        super(null);

        this.songControl = songControl;
        this.songControl.setEventGraphicUnit(this);

        this.trackLabels = new ArrayList<>();
        this.graphicEvents = new ArrayList<>();

        this.cursorLabel = new JLabel();
        this.cursorLabel.setOpaque(true);
        this.cursorLabel.setBackground(Color.BLUE);

        this.add(this.cursorLabel);
        this.cursorLabel.setLocation(0, 0);

        this.barRoster = TimeSignature.ONE_FOUR;

        this.toggleCtrl = false;

        this.defaultCurveType = CurveType.CONSTANT;
        this.curveBrush = false;

        this.setOpaque(true);
        this.setBackground(new Color(218, 218, 218));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    Point closestEventTime = getClosestEventTime(e.getX());
                    songControl.onSkipTo(closestEventTime.x);
                }
                repaint();
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                onKeyReleased(e);
            }
        });
    }

    public TimeSignature getBarRoster() {
        return this.barRoster;
    }
    public void setBarRoster(TimeSignature barRoster) {
        this.barRoster = barRoster;
        this.repaint();
    }

    public GraphicEvent findGraphicEvent(int trackIndex, int ms) {
        for(GraphicEvent graphicEvent : this.graphicEvents.get(trackIndex)) {
            Point eventTime = graphicEvent.getEventTime();
            if(ms >= eventTime.x && ms < eventTime.x + eventTime.y) {
                return graphicEvent;
            }
        }
        return null;
    }

    public void onKeyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                this.curveBrush = false;
            }
            case KeyEvent.VK_CONTROL -> this.toggleCtrl = true;
            case KeyEvent.VK_PLUS -> {
                if(this.toggleCtrl) {
                    zoom(true);
                }
            }
            case KeyEvent.VK_MINUS -> {
                if(this.toggleCtrl) {
                    zoom(false);
                }
            }
        }
    }
    public void onKeyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_CONTROL -> this.toggleCtrl = false;
        }
    }

    public void zoom(boolean zoomIn) {
        int oldX = this.getX() * (int)this.eventWidthDivision;
        if(zoomIn && this.eventWidthDivision >= 4.0) {
            this.eventWidthDivision -= 2.0;
        } else if(!zoomIn && this.eventWidthDivision <= 28.0) {
            this.eventWidthDivision += 2.0;
        }
        for(ArrayList<GraphicEvent> graphicEventArrayList : this.graphicEvents) {
            for(GraphicEvent graphicEvent : graphicEventArrayList) {
                graphicEvent.updateBounds();
            }
        }
        updateBounds();
        this.setLocation((int)Math.round(oldX / this.eventWidthDivision), 0);
        repaint();
    }

    private void updateBounds() {
        int totalWidth = 0;
        for(TimeMeasure timeMeasure : this.songControl.getTimeMeasures()) {
            int msSingleBeat = timeMeasure.getLengthOneBeat();
            totalWidth += (msSingleBeat * timeMeasure.getBeatsDuration());
        }
        this.setPreferredSize(new Dimension(
                totalWidth / (int)this.eventWidthDivision,
                this.getPreferredSize().height)
        );
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
                for(int i = 0; i < (int)Math.floor(timeMeasure.getBeatsDuration() * (1.0 / this.barRoster.getRatio())); i++) {
                    int x = (int)Math.floor((timeMeasure.getMsStart() / this.eventWidthDivision)
                    + ((this.barRoster.getRatio() * (double)i) * (msFullBar / this.eventWidthDivision)));
                    g.drawLine(x, 0, x, this.getHeight());
                }

                g.setColor(Color.BLACK);
                for(int i = 0; i < timeMeasure.getBeatsDuration(); i += timeMeasure.getBeatsPerBar()) {
                    int x = (int)Math.floor((timeMeasure.getMsStart() / this.eventWidthDivision)
                            + ((double)i * (msSingleBeat / this.eventWidthDivision)));
                    g.drawLine(x, 0, x, this.getHeight());
                }
            }
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        this.cursorLabel.setBounds(
                this.cursorLabel.getX(),
                this.cursorLabel.getY(),
                2,
                this.getSize().height
        );
    }

    @Override
    public void syncTracks(TrackTime[] trackTimes) {

        while(this.trackLabels.size() != 0) {
            this.trackLabels.remove(0);
        }
        while(this.graphicEvents.size() != 0) {
            this.graphicEvents.remove(0);
        }

        this.updateBounds();

        for(int i = 0; i < trackTimes.length; i++) {
            this.addTrack();
            for(int j = 0; j < trackTimes[i].getPoints().length; j++) {
                Point point = trackTimes[i].getPoints()[j];
                this.addEventToTrack(i, point.x, point.y, trackTimes[i].getCurveTypes()[j]);
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
       this.setPreferredSize(new Dimension(
               this.getPreferredSize().width,
               this.getPreferredSize().height + 50
       ));

       int currentIndex = this.trackLabels.size();

       trackLabel.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               if(!curveBrush) {
                   if(e.getClickCount() == 2 && e.getButton()  == MouseEvent.BUTTON1) {
                       Point eventTime = getClosestEventTime(e.getX());
                       songControl.onAddEventToTrackRequest(
                               currentIndex,
                               eventTime.x,
                               eventTime.y,
                               getDefaultCurveType()
                       );
                   }
               }
           }
       });

       this.trackLabels.add(trackLabel);
       this.add(trackLabel);

        JLabel numberLabel = new JLabel("" + this.trackLabels.size());
        numberLabel.setBounds(
                5,
                5 + ((this.trackLabels.size() - 1) * 50),
                20,
                10
        );
        this.add(numberLabel);

       this.graphicEvents.add(new ArrayList<>());

       this.repaint();
    }

    @Override
    public void removeTrack(int trackNumber) {
        this.trackLabels.remove(trackNumber);
        //TODO : trackIndexes aller GraphicEvents müssen aktualisiert werden
    }

    @Override
    public void addEventToTrack(int trackNumber, int msStart, int msDuration, CurveType curveType) {
        GraphicEvent graphicEvent = new GraphicEvent(trackNumber, curveType, msStart, msDuration, this, this.songControl);

        this.graphicEvents.get(trackNumber).add(graphicEvent);
        this.add(graphicEvent);
        graphicEvent.getParent().setComponentZOrder(graphicEvent, 0);
        this.repaint();
    }

    @Override
    public void deleteEvent(int trackNumber, int eventIndex) {
        this.remove(this.graphicEvents.get(trackNumber).get(eventIndex));
        this.graphicEvents.get(trackNumber).remove(eventIndex);
        this.repaint();
    }

    @Override
    public void editEvent(int trackIndex, int msStartOld, int msStartNew, int msDurationNew, CurveType curveTypeNew) {
        GraphicEvent graphicEvent = this.findGraphicEvent(trackIndex, msStartOld);
        graphicEvent.updateEventTime(msStartNew, msDurationNew);
        graphicEvent.setCurveType(curveTypeNew);
        graphicEvent.updateBounds();
    }

    @Override
    public void tick(int ms) {

        this.cursorLabel.setBounds(
                (int)Math.round(ms / this.eventWidthDivision),
                0,
                this.cursorLabel.getWidth(),
                this.cursorLabel.getHeight()
        );

        for(int i = 0; i < this.trackLabels.size(); i++) {
            int currentEventIndex = this.songControl.getCorrespondingEventIndex(i, ms);
            for(int j = 0; j < this.graphicEvents.get(i).size(); j++) {
                if(currentEventIndex == j) {
                    this.graphicEvents.get(i).get(j).setEnabled(false);
                } else if(!this.graphicEvents.get(i).get(j).isEnabled()) {
                    this.graphicEvents.get(i).get(j).setEnabled(true);
                }
            }
        }
    }

    @Override
    public double getEventWidthDivision() {
        return this.eventWidthDivision;
    }

    @Override
    public Point getClosestEventTime(int pixelInPanel) {
        int msInSong = pixelInPanel * (int)this.eventWidthDivision;
        TimeMeasure timeMeasure = this.songControl.getCorrespondingTimeMeasure(msInSong);
        double msOneSection = timeMeasure.getLengthOneBar() * this.barRoster.getRatio();
        int msInTimeMeasure = msInSong - timeMeasure.getMsStart();
        return new Point(
                (msInSong) - (int)Math.floor(msInTimeMeasure % msOneSection),
                (int)Math.round(msOneSection)
        );
    }

    @Override
    public void onSelectRequest(int trackIndex, int msStart) {
        GraphicEvent graphicEvent = this.findGraphicEvent(trackIndex, msStart);
        if(graphicEvent != null) {

        }
    }

    @Override
    public CurveType getDefaultCurveType() {
        return this.defaultCurveType;
    }
    @Override
    public void setDefaultCurveType(CurveType curveType) {
        this.defaultCurveType = curveType;
    }

    @Override
    public boolean getCurveBrush() {
        return this.curveBrush;
    }
    @Override
    public void setCurveBrush(boolean curveBrush) {
        this.curveBrush = curveBrush;
    }
}
