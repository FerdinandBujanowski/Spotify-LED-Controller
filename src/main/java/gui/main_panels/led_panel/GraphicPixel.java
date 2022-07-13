package gui.main_panels.led_panel;

import control.led.LedGraphicUnit;
import control.led.LedRequestAcceptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphicPixel extends JLabel {

    private int pixelX;
    private int pixelY;
    private final int pixelIndex;

    private boolean orderMode, ordered;
    private boolean inSelection;

    private final LedRequestAcceptor ledRequestAcceptor;
    private final LedGraphicUnit ledGraphicUnit;

    public GraphicPixel(LedRequestAcceptor ledRequestAcceptor, LedGraphicUnit ledGraphicUnit, int index, int x, int y) {
        super("", SwingConstants.CENTER);
        this.pixelIndex = index;
        this.pixelX = x;
        this.pixelY = y;
        this.orderMode = false;
        this.ordered = false;
        this.ledRequestAcceptor = ledRequestAcceptor;
        this.ledGraphicUnit = ledGraphicUnit;

        this.setOpaque(true);
        this.setForeground(Color.WHITE);
        this.showIndex(ledGraphicUnit.isShowIndexes());

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(outsideOfBounds(e.getX(), e.getY()) && !orderMode) {
                    int newX = getPixelX(), newY = getPixelY();
                    if(e.getX() < 0) newX--;
                    else if(e.getX() > getWidth()) newX++;
                    if(e.getY() < 0) newY--;
                    else if(e.getY() > getHeight()) newY++;

                    ledRequestAcceptor.onUpdatePixelRequest(pixelIndex, newX, newY, false);
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(orderMode) {
                    ledGraphicUnit.requestPixelOrdered(pixelIndex);
                } else {
                    ledGraphicUnit.handleSelection(pixelIndex);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                ledGraphicUnit.updatePixelBounds();
            }
        });
    }

    public int getPixelX() {
        return this.pixelX;
    }
    public int getPixelY() {
        return this.pixelY;
    }

    public int getPixelIndex() {
        return this.pixelIndex;
    }

    public void setNewCoordinates(int newX, int newY) {
        this.pixelX = newX;
        this.pixelY = newY;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(this.inSelection ? Color.BLUE : Color.WHITE);
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    }

    private boolean outsideOfBounds(int x, int y) {
        if(x < 0 || x > this.getWidth()) return true;
        else return y < 0 || y > this.getHeight();
    }

    public void showIndex(boolean showIndex) {
        if(showIndex) {
            this.setText(String.valueOf(this.pixelIndex));
        } else {
            this.setText("");
        }
    }

    public void setOrderMode(boolean orderMode) {
        this.orderMode = orderMode;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public void setInSelection(boolean inSelection) {
        this.inSelection = inSelection;
    }
}
