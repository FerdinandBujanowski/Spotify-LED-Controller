package gui.main_panels.event_panel;

import control.event.*;
import control.type_enums.CurveType;
import control.type_enums.TimeSignature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EventEditWindow extends JPanel implements EventGraphicUnit {

    private final EventRequestAcceptor eventControl;

    private final ArrayList<GraphicTimeMeasure> graphicTimeMeasures;
    private final ArrayList<JLabel> trackLabels;
    private final ArrayList<ArrayList<GraphicEvent>> graphicEvents;
    private final JLabel cursorLabel;

    private double eventWidthDivision = 20;
    private TimeSignature barRoster;

    private CurveType defaultCurveType;
    private boolean curveBrush;

    private final ArrayList<Point> selectedEvents;
    private final ArrayList<Point> copiedEvents;
    private boolean toggleShift, toggleCtrl, toggleG;
    private boolean initiateToggleG;
    private int movingOffset;

    public EventEditWindow(EventRequestAcceptor eventControl) {
        super(null);

        this.eventControl = eventControl;
        this.eventControl.setEventGraphicUnit(this);

        this.graphicTimeMeasures = new ArrayList<>();
        this.trackLabels = new ArrayList<>();
        this.graphicEvents = new ArrayList<>();

        this.cursorLabel = new JLabel();
        this.cursorLabel.setOpaque(true);
        this.cursorLabel.setBackground(Color.BLUE);

        this.add(this.cursorLabel);
        this.cursorLabel.setLocation(0, 0);

        this.barRoster = TimeSignature.ONE_FOUR;

        this.defaultCurveType = CurveType.CONSTANT;
        this.curveBrush = false;

        this.selectedEvents = new ArrayList<>();
        this.copiedEvents = new ArrayList<>();

        this.setOpaque(true);
        this.setBackground(new Color(25, 20, 20));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toggleG = false;
                removeWholeSelection();
                requestFocus();
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    Point closestEventTime = getClosestEventTime(e.getX());
                    eventControl.onSkipTo(closestEventTime.x);
                    repaint();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                for(GraphicTimeMeasure graphicTimeMeasure : graphicTimeMeasures) {
                    graphicTimeMeasure.resetHoveredBar();
                    repaint();
                }
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleG(e.getX());
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
                repaint();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                onKeyReleased(e);
                repaint();
            }
        });

        eventControl.onAddTimeMeasureRequest(4, 60, 0, 16);
        eventControl.onAddTrackRequest();
    }

    public TimeSignature getBarRoster() {
        return this.barRoster;
    }
    public void setBarRoster(TimeSignature barRoster) {
        this.barRoster = barRoster;
        this.repaint();
    }

    public GraphicEvent findGraphicEvent(int trackIndex, int eventIndex) {
        if(this.graphicEvents.size() <= trackIndex || this.graphicEvents.get(trackIndex).size() <= eventIndex) {
            return null;
        }
        return this.graphicEvents.get(trackIndex).get(eventIndex);
    }

    public void onKeyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> this.curveBrush = false;
            case KeyEvent.VK_SHIFT -> {
                if(!this.toggleCtrl) {
                    this.toggleShift = true;
                }
            }
            case KeyEvent.VK_CONTROL -> {
                if(!this.toggleShift) {
                    this.toggleCtrl = true;
                    this.repaint();
                }
            }
            case KeyEvent.VK_G -> {
                this.toggleG = !this.toggleG;
                initiateToggleG = true;
            }
            case KeyEvent.VK_J -> {
                if(this.selectedEvents.size() > 0) {
                    int lastSelectedTrack = this.selectedEvents.get(this.selectedEvents.size() - 1).x;
                    this.removeWholeSelection();
                    this.selectWholeTrack(lastSelectedTrack);
                }
            }
            case KeyEvent.VK_C -> {
                if(this.toggleCtrl) {
                    this.copiedEvents.removeAll(this.copiedEvents);
                    this.copiedEvents.addAll(this.selectedEvents);
                }
            }
            case KeyEvent.VK_V -> {
                if(this.toggleCtrl) {
                    ArrayList<Point> newEventIndexes = this.eventControl.onCopyEventsRequest(this.copiedEvents);
                    this.removeWholeSelection();
                    this.selectedEvents.addAll(newEventIndexes);
                    for(Point currentEvent : newEventIndexes) {
                        GraphicEvent graphicEvent = this.findGraphicEvent(currentEvent.x, currentEvent.y);
                        if(graphicEvent != null) {
                            graphicEvent.setInSelection(true);
                        }
                    }
                    this.toggleG = true;
                    this.initiateToggleG = true;
                }
            }
            case KeyEvent.VK_A -> {
                if(this.toggleCtrl) {
                    this.removeWholeSelection();
                    this.selectAllTracks();
                }
            }
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
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.toggleShift = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.toggleCtrl = false;
            this.repaint();
        }
    }

    public void zoom(boolean zoomIn) {
        int oldX = this.getX() * (int)this.eventWidthDivision;
        if(zoomIn && this.eventWidthDivision >= 4.0) {
            this.eventWidthDivision -= 2.0;
        } else if(!zoomIn && this.eventWidthDivision <= 28.0) {
            this.eventWidthDivision += 2.0;
        }
        for(GraphicTimeMeasure graphicTimeMeasure : this.graphicTimeMeasures) {
            graphicTimeMeasure.updateBounds();
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

    private void handleG(int x) {
        if(!this.toggleG) return;

        if(this.initiateToggleG) {
            this.initiateToggleG = false;
            this.movingOffset = this.getClosestEventTime(x).x;
        }
        int relativeMovement = this.getClosestEventTime(x).x - this.movingOffset;
        if(relativeMovement == 0) return;

        for(Point selectedEvent : this.selectedEvents) {
            GraphicEvent graphicEvent = this.findGraphicEvent(selectedEvent.x, selectedEvent.y);
            if(graphicEvent != null) {
                this.eventControl.onUpdateEventRequest(
                        selectedEvent.x,
                        selectedEvent.y,
                        graphicEvent.getEventTime().x,
                        false,
                        graphicEvent.getCurveType(),
                        graphicEvent.getUserInput(),
                        graphicEvent.getEventTime().x + relativeMovement,
                        graphicEvent.getEventTime().y
                );
            }
        }
        this.movingOffset = this.movingOffset + relativeMovement;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ArrayList<TimeMeasure> timeMeasures = this.eventControl.getTimeMeasures();
        if(timeMeasures != null) {
            for(TimeMeasure timeMeasure : timeMeasures) {

                double msSingleBeat = 1000.0 / (timeMeasure.getBeatsPerMinute() / 60.0);
                double msFullBar = msSingleBeat * timeMeasure.getBeatsPerBar();

                g.setColor(Color.GRAY);
                for(int i = 0; i < (int)Math.floor(timeMeasure.getBarsDuration() * timeMeasure.getBeatsPerBar() * (1.0 / this.barRoster.getRatio())); i++) {
                    int x = (int)Math.floor((timeMeasure.getMsStart() / this.eventWidthDivision)
                    + ((this.barRoster.getRatio() * (double)i) * (msFullBar / this.eventWidthDivision)));
                    g.drawLine(x, 0, x, this.getHeight());
                }

                g.setColor(Color.WHITE);
                for(int i = 0; i < timeMeasure.getBarsDuration() * timeMeasure.getBeatsPerBar(); i += timeMeasure.getBeatsPerBar()) {
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
                //TODO user input
                this.addEventToTrack(i, point.x, point.y, trackTimes[i].getCurveTypes()[j], 0);
            }
        }

        this.repaint();
    }

    @Override
    public void updateBounds() {
        int totalWidth = 0;
        for(TimeMeasure timeMeasure : this.eventControl.getTimeMeasures()) {
            int msSingleBeat = timeMeasure.getLengthOneBeat();
            totalWidth += (msSingleBeat * timeMeasure.getBarsDuration() * timeMeasure.getBeatsPerBar());
        }
        this.setPreferredSize(new Dimension(
                totalWidth / (int)this.eventWidthDivision,
                this.getPreferredSize().height)
        );

        for(int i = 0; i < trackLabels.size(); i++) {
            trackLabels.get(i).setBounds(
                    0,
                    20 + (i * 50),
                    this.getPreferredSize().width,
                    30
            );
        }
    }

    @Override
    public void addTimeMeasure(TimeMeasure timeMeasure) {
        GraphicTimeMeasure graphicTimeMeasure = new GraphicTimeMeasure(timeMeasure, this.eventControl, this);
        this.graphicTimeMeasures.add(graphicTimeMeasure);
        this.add(graphicTimeMeasure);
        graphicTimeMeasure.getParent().setComponentZOrder(graphicTimeMeasure, 0);
    }

    @Override
    public void removeTimeMeasure(int msStart) {
        for(GraphicTimeMeasure graphicTimeMeasure : this.graphicTimeMeasures) {
            if(graphicTimeMeasure.getTimeMeasure().getMsStart() == msStart) {
                this.graphicTimeMeasures.remove(graphicTimeMeasure);
                this.remove(graphicTimeMeasure);
                repaint();
                return;
            }
        }
    }

    @Override
    public void addTrack() {

       JLabel trackLabel = new JLabel();
       trackLabel.setOpaque(true);
       this.setBackground(new Color(25, 20, 20));

       this.setPreferredSize(new Dimension(
               this.getPreferredSize().width,
               this.getPreferredSize().height + 50
       ));

       int currentIndex = this.trackLabels.size();

       trackLabel.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent e) {
               if(!curveBrush) {
                   if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                       Point eventTime = getClosestEventTime(e.getX());
                       eventControl.onAddEventToTrackRequest(
                               currentIndex,
                               eventTime.x,
                               eventTime.y,
                               getDefaultCurveType(),
                               0
                       );
                   }
               }
           }
       });
       trackLabel.addMouseMotionListener(new MouseMotionAdapter() {
           @Override
           public void mouseMoved(MouseEvent e) {
               handleG(e.getX());
           }
       });

       this.trackLabels.add(trackLabel);
       this.add(trackLabel);

        JLabel numberLabel = new JLabel("" + (this.trackLabels.size() - 1));
        numberLabel.setBounds(
                5,
                5 + ((this.trackLabels.size() - 1) * 50),
                20,
                10
        );
        this.add(numberLabel);

       this.graphicEvents.add(new ArrayList<>());

       this.updateBounds();
       this.repaint();
    }

    @Override
    public void removeTrack(int trackNumber) {
        this.trackLabels.remove(trackNumber);
        //TODO : trackIndexes aller GraphicEvents müssen aktualisiert werden
    }

    @Override
    public void addEventToTrack(int trackIndex, int msStart, int msDuration, CurveType curveType, double userInput) {
        int newEventIndex = this.graphicEvents.get(trackIndex).size();
        GraphicEvent graphicEvent = new GraphicEvent(trackIndex, newEventIndex, curveType, msStart, msDuration, this, this.eventControl);
        graphicEvent.setUserInput(userInput);

        this.graphicEvents.get(trackIndex).add(graphicEvent);
        this.add(graphicEvent);
        graphicEvent.getParent().setComponentZOrder(graphicEvent, 0);
        this.repaint();
    }

    @Override
    public void deleteEvent(int trackIndex, int eventIndex) {
        this.remove(this.graphicEvents.get(trackIndex).get(eventIndex));
        this.graphicEvents.get(trackIndex).remove(eventIndex);
        for(int i = eventIndex; i < this.graphicEvents.get(trackIndex).size(); i++) {
            this.graphicEvents.get(trackIndex).get(i).pushBackIndex();
        }
        this.repaint();
    }

    @Override
    public void editEvent(int trackIndex, int eventIndex, int msStartNew, int msDurationNew, CurveType curveTypeNew, double userInput) {
        GraphicEvent graphicEvent = this.findGraphicEvent(trackIndex, eventIndex);
        graphicEvent.updateEventTime(msStartNew, msDurationNew);
        graphicEvent.setCurveType(curveTypeNew);
        graphicEvent.setUserInput(userInput);
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
            int currentEventIndex = this.eventControl.getCorrespondingEventIndex(i, ms);
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
        TimeMeasure timeMeasure = this.eventControl.getCorrespondingTimeMeasure(msInSong);
        double msOneSection = timeMeasure.getLengthOneBar() * this.barRoster.getRatio();
        int msInTimeMeasure = msInSong - timeMeasure.getMsStart();
        return new Point(
                (msInSong) - (int)Math.floor(msInTimeMeasure % msOneSection),
                (int)Math.round(msOneSection)
        );
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

    @Override
    public void onSelectionEvent(int trackIndex, int eventIndex) {
        GraphicEvent graphicEvent = this.findGraphicEvent(trackIndex, eventIndex);
        if(graphicEvent == null) return;

        Point selection = new Point(trackIndex, eventIndex);

        if(this.toggleShift) {
            if(!this.selectedEvents.contains(selection)) {
                this.selectedEvents.add(selection);
                graphicEvent.setInSelection(true);
            }
        } else if(this.toggleCtrl) {
            this.selectedEvents.remove(selection);
            graphicEvent.setInSelection(false);
        } else {
            this.removeWholeSelection();
            this.selectedEvents.add(selection);
            graphicEvent.setInSelection(true);
        }
        this.repaint();
    }

    private void removeWholeSelection() {
        this.selectedEvents.removeAll(this.selectedEvents);
        for(ArrayList<GraphicEvent> allGraphicEvents : this.graphicEvents) {
            for(GraphicEvent everyGraphicEvent : allGraphicEvents) {
                everyGraphicEvent.setInSelection(false);
            }
        }
    }

    private void selectWholeTrack(int trackIndex) {
        ArrayList<GraphicEvent> currentTrack = this.graphicEvents.get(trackIndex);
        for(int i = 0; i < currentTrack.size(); i++) {
            Point selection = new Point(trackIndex, i);
            if(!this.selectedEvents.contains(selection)) {
                this.selectedEvents.add(selection);
                currentTrack.get(i).setInSelection(true);
            }
        }
    }

    private void selectAllTracks() {
        for(int i = 0; i < this.graphicEvents.size(); i++) {
            this.selectWholeTrack(i);
        }
    }
}
