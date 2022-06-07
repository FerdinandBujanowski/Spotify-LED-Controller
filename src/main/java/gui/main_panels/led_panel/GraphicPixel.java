package gui.main_panels.led_panel;

import control.led.LedRequestAcceptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphicPixel extends JLabel {

    private int pixelX;
    private int pixelY;

    private LedRequestAcceptor ledRequestAcceptor;

    public GraphicPixel(LedRequestAcceptor ledRequestAcceptor, int x, int y) {
        this.pixelX = x;
        this.pixelY = y;
        this.ledRequestAcceptor = ledRequestAcceptor;
        
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
            }
        });
    }

    public int getPixelX() {
        return this.pixelX;
    }
    public int getPixelY() {
        return this.pixelY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(this.ledRequestAcceptor.getColorAt(this.pixelX, this.pixelY));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if(width == 0 && height == 0) {
            System.out.println("AA");
            Exception exception = new Exception("test");
            exception.printStackTrace();
        }
    }
}
