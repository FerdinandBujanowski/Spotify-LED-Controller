package gui.main_panels.event_panel;

import control.event.EventGraphicUnit;
import control.event.TimeMeasure;
import control.event.EventRequestAcceptor;
import control.type_enums.CurveType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphicTimeMeasure extends GraphicEvent {

    private TimeMeasure timeMeasure;
    private EventRequestAcceptor eventRequestAcceptor;
    private EventGraphicUnit eventGraphicUnit;

    private int hoveredBar;

    private boolean startLock, midLock, endLock;
    private JCheckBoxMenuItem startLockItem, midLockItem, endLockItem;

    public GraphicTimeMeasure(TimeMeasure timeMeasure, EventRequestAcceptor eventRequestAcceptor, EventGraphicUnit eventGraphicUnit) {
        super(
                0, 0,
                null,
                timeMeasure.getMsStart(),
                (timeMeasure.getLengthOneBar() * timeMeasure.getBarsDuration()),
                eventGraphicUnit,
                eventRequestAcceptor
        );

        this.timeMeasure = timeMeasure;
        this.eventRequestAcceptor = eventRequestAcceptor;
        this.eventGraphicUnit = eventGraphicUnit;

        this.startLockItem = new JCheckBoxMenuItem("Start Lock");
        this.midLockItem = new JCheckBoxMenuItem("Mid Lock");
        this.endLockItem = new JCheckBoxMenuItem("End Lock");

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu editMenu = new JPopupMenu();

                    JMenuItem splitMenuItem = new JMenuItem("Split");
                    splitMenuItem.addActionListener(actionEvent -> {
                        eventRequestAcceptor.onSplitTimeMeasureRequest(timeMeasure.getMsStart(), hoveredBar);
                    });


                    ActionListener actionListener = actionEvent -> {
                        handleLock(startLockItem.isSelected(), midLockItem.isSelected(), endLockItem.isSelected());
                    };
                    startLockItem.addActionListener(actionListener);
                    midLockItem.addActionListener(actionListener);
                    endLockItem.addActionListener(actionListener);

                    editMenu.add(splitMenuItem);
                    editMenu.add(new JSeparator());
                    editMenu.add(startLockItem);
                    editMenu.add(midLockItem);
                    editMenu.add(endLockItem);

                    editMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                calculateHoveredBarX(e.getX());
            }
        });
    }

    public void calculateHoveredBarX(int pixelX) {
        int ms = (int)Math.round(pixelX * this.eventGraphicUnit.getEventWidthDivision());
        this.hoveredBar = (int)Math.round((double)ms / this.timeMeasure.getLengthOneBar());
    }

    public void handleLock(boolean startLockRequest, boolean midLockRequest, boolean endLockRequest) {
        //TODO : manche kombinationen ergeben keinen sinn
        this.startLock = startLockRequest;
        this.startLockItem.setSelected(this.startLock);
        this.midLock = midLockRequest;
        this.midLockItem.setSelected(this.midLock);
        this.endLock = endLockRequest;
        this.endLockItem.setSelected(this.endLock);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.MAGENTA.darker());
        g.fillRect(3, 3, this.getWidth() - 6, this.getHeight() - 6);

        int totalBeats = this.timeMeasure.getBeatsPerBar() * this.timeMeasure.getBarsDuration();
        double msSingleBeat = 1000.0 / (this.timeMeasure.getBeatsPerMinute() / 60.0);
        for(int i = 1; i < totalBeats; i++) {
            g.setColor(i % this.timeMeasure.getBeatsPerBar() == 0 ? Color.WHITE : Color.GRAY);
            int x = (int)Math.floor(i * msSingleBeat / this.eventGraphicUnit.getEventWidthDivision());
            g.fillRect(x, 0, 1, this.getHeight());
        }

        if(this.isRightHovered() || this.getLastMovement() == 1) {
            g.setColor(Color.BLUE);
            g.fillRect(this.getWidth() - 2, 0, 2, this.getHeight());
        }
        if(this.isLeftHovered() || this.getLastMovement() == -1) {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, 2, this.getHeight());
        }

        if(this.hoveredBar != 0) {
            g.setColor(Color.YELLOW);
            int x = (int)Math.round(this.hoveredBar * this.timeMeasure.getLengthOneBar() / this.eventGraphicUnit.getEventWidthDivision());
            g.fillRect(x - 2, 0, 4, this.getHeight());
        }
    }

    public void resetHoveredBar() {
        this.hoveredBar = 0;
    }

    public TimeMeasure getTimeMeasure() {
        return this.timeMeasure;
    }
}
