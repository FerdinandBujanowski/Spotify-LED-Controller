package gui.main_panels.led_panel;

import control.led.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LedEditWindow extends JPanel implements LedGraphicUnit {

    private LedRequestAcceptor ledControl;
    private int finalDegree;
    private boolean drawOnlyLedPixels;

    private ArrayList<GraphicPixel> graphicPixels;
    private LayersPanel layersPanel;
    private JPanel pixelPanel;

    public LedEditWindow(LedRequestAcceptor ledControl, Dimension windowDimension) {
        super(null);

        this.ledControl = ledControl;
        this.ledControl.setLedGraphicUnit(this);
        this.finalDegree = 0;

        this.drawOnlyLedPixels = false;

        this.graphicPixels = new ArrayList<>();
        this.layersPanel = new LayersPanel(ledControl, this);
        this.pixelPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //super.paintComponent(g);

                int finalDegree = ledControl.getFinalDegree();
                int pixelLength = (finalDegree * 2) + 1;
                int step = (int)Math.round(this.getWidth() / (double)pixelLength);

                if(!drawOnlyLedPixels) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    for(int i = -finalDegree; i <= finalDegree; i++) {
                        for(int j = -finalDegree; j <= finalDegree; j++) {
                            g.setColor(ledControl.getColorAt(i, j));
                            g.fillRect(
                                    (i + finalDegree) * step,
                                    (j + finalDegree) * step,
                                    step, step
                            );
                        }
                    }
                }
            }
        };
        this.add(this.pixelPanel);

        this.setOpaque(true);
        this.setBackground(new Color(40, 40, 40));
        this.resizeComponents(windowDimension);
    }

    public void resizeComponents(Dimension dimension) {
        this.pixelPanel.setBounds(
                ((dimension.width - 200) / 2) - 200,
                30,
                400, 400
        );
        this.updatePixelBounds();
    }

    public JPanel getLayersPanel() {
        return this.layersPanel;
    }

    @Override
    public void addPixel(int x, int y) {
        GraphicPixel newPixel = new GraphicPixel(this.ledControl, this, x, y);
        this.graphicPixels.add(newPixel);
        this.add(newPixel);
        this.setComponentZOrder(newPixel, 0);
    }

    @Override
    public void movePixel(int index, int newX, int newY) {
        GraphicPixel graphicPixel = this.graphicPixels.get(index);
        graphicPixel.setNewCoordinates(newX, newY);
        this.updatePixelPositions();
    }
    @Override
    public void deletePixel(int index) {
        this.remove(this.graphicPixels.get(index));
    }

    @Override
    public void setDrawOnlyLedPixels(boolean drawOnlyLedPixels) {
        this.drawOnlyLedPixels = drawOnlyLedPixels;
    }

    @Override
    public void addLayer(int newIndex) {
        this.layersPanel.addLayer(newIndex);
        this.layersPanel.repaint();
        this.repaint();
    }

    @Override
    public void updatePixelBounds() {
        this.finalDegree = this.ledControl.getFinalDegree();
        this.updatePixelPositions();
    }

    @Override
    public void updatePixelPositions() {
        int pixelLength = (this.finalDegree * 2) + 1;
        int step = (int)Math.round(this.pixelPanel.getWidth() / (double)pixelLength);

        for(GraphicPixel graphicPixel : this.graphicPixels) {
            graphicPixel.setBounds(
                    this.pixelPanel.getX() + ((graphicPixel.getPixelX() + this.finalDegree) * step),
                    this.pixelPanel.getY() + ((graphicPixel.getPixelY() + this.finalDegree) * step),
                    step, step
            );
        }
        this.repaint();
    }
}