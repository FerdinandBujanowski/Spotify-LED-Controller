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

    private final LedRequestAcceptor ledRequestAcceptor;
    private final LedGraphicUnit ledGraphicUnit;

    public GraphicPixel(LedRequestAcceptor ledRequestAcceptor, LedGraphicUnit ledGraphicUnit, int x, int y) {
        this.pixelX = x;
        this.pixelY = y;
        this.ledRequestAcceptor = ledRequestAcceptor;
        this.ledGraphicUnit = ledGraphicUnit;

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(outsideOfBounds(e.getX(), e.getY())) {
                    int oldX = getPixelX(), oldY = getPixelY();
                    int newX = getPixelX(), newY = getPixelY();
                    if(e.getX() < 0) newX--;
                    else if(e.getX() > getWidth()) newX++;
                    if(e.getY() < 0) newY--;
                    else if(e.getY() > getHeight()) newY++;

                    ledRequestAcceptor.onUpdatePixelRequest(oldX, oldY, newX, newY, false);
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
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

    public void setNewCoordinates(int newX, int newY) {
        this.pixelX = newX;
        this.pixelY = newY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(this.ledRequestAcceptor.getColorAt(this.pixelX, this.pixelY));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    }

    private boolean outsideOfBounds(int x, int y) {
        if(x < 0 || x > this.getWidth()) return true;
        else return y < 0 || y > this.getHeight();
    }
}
