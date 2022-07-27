package gui.main_panels.event_panel;

import control.event.EventGraphicUnit;
import control.event.EventRequestAcceptor;
import control.type_enums.CurveType;
import gui.Dialogues;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphicEvent extends JLabel {

    private EventGraphicUnit eventGraphicUnit;
    private EventRequestAcceptor eventControl;

    private CurveType curveType;
    private double userInput;
    private final int trackIndex, eventIndex;
    private Point eventTime;

    private boolean leftHovered, rightHovered;
    private int lastMovement;
    private int differenceOnClicked;

    private boolean inSelection;

    public GraphicEvent(int trackIndex, int eventIndex, CurveType curveType, int msStart, int msDuration, EventGraphicUnit eventGraphicUnit, EventRequestAcceptor eventControl) {
        this.trackIndex = trackIndex;
        this.eventIndex = eventIndex;

        this.curveType = curveType;
        this.eventTime = new Point();
        this.eventGraphicUnit = eventGraphicUnit;
        this.eventControl = eventControl;

        this.leftHovered = false;
        this.rightHovered = false;
        this.lastMovement = 0;
        this.differenceOnClicked = 0;

        this.inSelection = false;

        this.setOpaque(true);
        this.setBackground(curveType != null ? curveType.getColor() : Color.WHITE);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if(!eventGraphicUnit.getCurveBrush()) {

                    eventGraphicUnit.onSelectionEvent(trackIndex, eventIndex);

                    int x = getX() + e.getX();
                    Point newTime = eventGraphicUnit.getClosestEventTime(x);
                    Point startTime = eventGraphicUnit.getClosestEventTime(getX());
                    differenceOnClicked = newTime.x - startTime.x;

                    if(leftHovered) {
                        lastMovement = -1;
                    }
                    if(rightHovered) {
                        lastMovement = 1;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if(!eventGraphicUnit.getCurveBrush() && curveType != null) {
                    if(e.getButton() == MouseEvent.BUTTON3) {

                        int selectedOption = Dialogues.getSelectedOptionFromArray(CurveType.values(), "Edit Curve Type", CurveType.indexOf(getCurveType()));
                        CurveType newCurveType = CurveType.values()[selectedOption];
                        if(newCurveType == CurveType.USER_INPUT) {
                            userInput = Dialogues.getNumberValue("Please enter Unit Number");
                            if (userInput < 0) userInput = 0;
                            else if (userInput > 1) userInput = 1;
                        }

                        eventControl.onUpdateEventRequest(
                                trackIndex,
                                eventIndex,
                                eventTime.x,
                                false,
                                newCurveType,
                                userInput,
                                eventTime.x,
                                eventTime.y
                        );
                    } else if(e.getButton() == MouseEvent.BUTTON1) {

                        if(e.getClickCount() == 2) {
                            eventControl.onUpdateEventRequest(
                                    trackIndex,
                                    eventIndex,
                                    eventTime.x,
                                    true,
                                    getCurveType(),
                                    userInput,
                                    0,
                                    0
                            );
                        }
                    }
                } else {
                    if(e.getButton() == MouseEvent.BUTTON1) {
                        eventControl.onUpdateEventRequest(
                                trackIndex,
                                eventIndex,
                                eventTime.x,
                                false,
                                eventGraphicUnit.getDefaultCurveType(),
                                userInput,
                                eventTime.x,
                                eventTime.y
                        );
                        repaint();
                    }
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if(!eventGraphicUnit.getCurveBrush()) {
                    leftHovered = false;
                    rightHovered = false;
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(!eventGraphicUnit.getCurveBrush()) {
                    lastMovement = 0;
                    repaint();
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(eventGraphicUnit.getCurveBrush()) return;

                int x = getX() + e.getX();
                Point newTime = eventGraphicUnit.getClosestEventTime(x);

                if(lastMovement == -1 && newTime.x < (eventTime.x + eventTime.y) && eventTime.x != newTime.x) {
                    eventControl.onUpdateEventRequest(
                            trackIndex,
                            eventIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            userInput,
                            newTime.x,
                            (eventTime.x - newTime.x) + eventTime.y
                    );
                } else if(lastMovement == 1 && (newTime.x + newTime.y) > eventTime.x && newTime.x != (eventTime.x + eventTime.y - newTime.y)) {
                    eventControl.onUpdateEventRequest(
                            trackIndex,
                            eventIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            userInput,
                            eventTime.x,
                            (newTime.x + newTime.y) - eventTime.x
                    );
                } else if(lastMovement == 0 && newTime.x != (eventTime.x + differenceOnClicked)) {
                    eventControl.onUpdateEventRequest(
                            trackIndex,
                            eventIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            userInput,
                            newTime.x - differenceOnClicked,
                            eventTime.y
                    );
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if(eventGraphicUnit.getCurveBrush()) return;

                if(e.getX() >= getWidth() - 10) {
                    if(!rightHovered) {
                        rightHovered = true;
                        repaint();
                    }
                } else {
                    if(rightHovered) {
                        rightHovered = false;
                        repaint();
                    }
                }
                if(e.getX() <= 10) {
                    if(!leftHovered) {
                        leftHovered = true;
                        repaint();
                    }
                } else {
                    if(leftHovered) {
                        leftHovered = false;
                        repaint();
                    }
                }
                repaint();
            }
        });

        this.updateEventTime(msStart, msDuration);
        this.updateBounds();
    }

    public Point getEventTime() {
        return this.eventTime;
    }
    public void updateEventTime(int msStart, int msDuration) {
        this.eventTime.x = msStart;
        this.eventTime.y = msDuration;
    }

    public void updateBounds() {
        this.setBounds(
                (int) Math.round(this.eventTime.x / eventGraphicUnit.getEventWidthDivision()),
                20 + (this.trackIndex * 50),
                (int) Math.round(this.eventTime.y / eventGraphicUnit.getEventWidthDivision()),
                30
        );
    }

    public CurveType getCurveType() {
        return this.curveType;
    }
    public void setCurveType(CurveType curveType) {
        this.curveType = curveType;
        this.setBackground(curveType.getColor());
    }

    public double getUserInput() {
        return this.userInput;
    }

    public void setUserInput(double userInput) {
        if(userInput > 1) this.userInput = 1;
        else if(userInput < 0) this.userInput = 0;
        else this.userInput = userInput;
    }

    public boolean isRightHovered() {
        return this.rightHovered;
    }
    public boolean isLeftHovered() {
        return this.leftHovered;
    }
    public int getLastMovement() {
        return this.lastMovement;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, this.getSize().width, this.getSize().height);

        if(this.inSelection) {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        g.setColor(this.curveType.getColor().darker());
        g.fillRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);


        g.setColor(Color.WHITE);
        if(!this.isEnabled()) {
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        for(int i = 0; i < this.getWidth(); i++) {
            double x = (double)i / (double)this.getWidth();
            double value = this.curveType == CurveType.USER_INPUT ? this.userInput : this.curveType.getCurve(x);
            g.fillRect(
                    i,
                    this.getHeight() - (int)Math.round(value * (double)this.getHeight()),
                    1,
                    1
            );
        }

        if(this.rightHovered || this.lastMovement == 1) {
            g.setColor(Color.BLUE);
            g.fillRect(this.getWidth() - 2, 0, 2, this.getHeight());
        }
        if(this.leftHovered || this.lastMovement == -1) {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, 2, this.getHeight());
        }
    }
}
