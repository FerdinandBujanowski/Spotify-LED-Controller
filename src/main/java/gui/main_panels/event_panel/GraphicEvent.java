package gui.main_panels.event_panel;

import control.event.EventGraphicUnit;
import control.event.TrackRequestAcceptor;
import control.type_enums.CurveType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphicEvent extends JLabel {

    private EventGraphicUnit eventGraphicUnit;
    private TrackRequestAcceptor songControl;

    private CurveType curveType;
    private int trackIndex;
    private Point eventTime;

    private boolean selected;
    private boolean leftHovered, rightHovered;
    private int lastMovement;

    public GraphicEvent(int trackIndex, CurveType curveType, int msStart, int msDuration, EventGraphicUnit eventGraphicUnit, TrackRequestAcceptor songControl) {
        this.trackIndex = trackIndex;
        this.curveType = curveType;
        this.eventTime = new Point();
        this.eventGraphicUnit = eventGraphicUnit;
        this.songControl = songControl;
        
        this.selected = false;
        this.leftHovered = false;
        this.rightHovered = false;
        this.lastMovement = 0;

        this.setOpaque(true);
        this.setBackground(curveType.getColor());

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if(!eventGraphicUnit.getCurveBrush()) {
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

                if(!eventGraphicUnit.getCurveBrush()) {
                    if(e.getButton() == MouseEvent.BUTTON3) {

                        JComboBox curveTypeComboBox = new JComboBox(CurveType.values());
                        curveTypeComboBox.setSelectedIndex(CurveType.indexOf(curveType));
                        JOptionPane.showOptionDialog(null, curveTypeComboBox, "Edit Curve Type", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                        CurveType newCurveType = CurveType.values()[curveTypeComboBox.getSelectedIndex()];

                        songControl.onUpdateEventRequest(
                                trackIndex,
                                eventTime.x,
                                false,
                                newCurveType,
                                eventTime.x,
                                eventTime.y
                        );
                    } else if(e.getButton() == MouseEvent.BUTTON1) {
                        if(e.getClickCount() == 2) {
                            songControl.onUpdateEventRequest(
                                    trackIndex,
                                    eventTime.x,
                                    true,
                                    getCurveType(),
                                    0,
                                    0
                            );
                        }
                    }
                } else {
                    if(e.getButton() == MouseEvent.BUTTON1) {
                        songControl.onUpdateEventRequest(
                                trackIndex,
                                eventTime.x,
                                false,
                                eventGraphicUnit.getDefaultCurveType(),
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
                    songControl.onUpdateEventRequest(
                            trackIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            newTime.x,
                            (eventTime.x - newTime.x) + eventTime.y
                    );
                } else if(lastMovement == 1 && (newTime.x + newTime.y) > eventTime.x && newTime.x != (eventTime.x + eventTime.y - newTime.y)) {
                    songControl.onUpdateEventRequest(
                            trackIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            eventTime.x,
                            (newTime.x + newTime.y) - eventTime.x
                    );
                } else if(lastMovement == 0 && newTime.x != eventTime.x) {
                    songControl.onUpdateEventRequest(
                            trackIndex,
                            eventTime.x,
                            false,
                            getCurveType(),
                            newTime.x,
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, this.getSize().width, this.getSize().height);
        if(!this.isEnabled()) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.WHITE);
        for(int i = 0; i < this.getWidth(); i++) {
            double x = (double)i / (double)this.getWidth();
            g.fillRect(
                    i,
                    this.getHeight() - (int)Math.round(this.curveType.getCurve(x) * (double)this.getHeight()),
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
