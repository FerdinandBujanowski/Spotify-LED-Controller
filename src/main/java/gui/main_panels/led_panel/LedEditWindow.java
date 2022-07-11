package gui.main_panels.led_panel;

import control.led.*;
import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LedEditWindow extends JPanel implements LedGraphicUnit {

    private LedRequestAcceptor ledControl;
    private int finalDegree;
    private int extraSpace;
    private boolean drawOnlyLedPixels;
    private boolean showIndexes;
    private boolean orderMode;
    private boolean updatePortWhenRepaint;

    private TwoIntegerCorrespondence newOrderCorrespondence;

    ArrayList<ThreeCoordinatePoint> newOrder;

    private ArrayList<GraphicPixel> graphicPixels;
    private LayersPanel layersPanel;
    private JPanel pixelPanel;

    public LedEditWindow(LedRequestAcceptor ledControl, Dimension windowDimension) {
        super(null);

        this.ledControl = ledControl;
        this.ledControl.setLedGraphicUnit(this);
        this.finalDegree = 0;
        this.extraSpace = 0;

        this.drawOnlyLedPixels = false;
        this.showIndexes = false;
        this.orderMode = false;
        this.newOrderCorrespondence = new TwoIntegerCorrespondence();

        this.newOrder = new ArrayList<>();

        this.graphicPixels = new ArrayList<>();
        this.layersPanel = new LayersPanel(ledControl, this);
        this.pixelPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                //super.paintComponent(g);

                int pixelLength = (finalDegree * 2) + 1;
                int step = (int)Math.round(this.getWidth() / (double)pixelLength);

                if(!drawOnlyLedPixels) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, this.getWidth(), this.getHeight());

                    for(int i = -finalDegree; i <= finalDegree; i++) {
                        for(int j = -finalDegree; j <= finalDegree; j++) {
                            g.setColor(ledControl.getColorAt(i, j));
                            g.fillRect(
                                    (i + finalDegree) * step + extraSpace,
                                    (j + finalDegree) * step + extraSpace,
                                    step, step
                            );
                        }
                    }
                    g.setColor(Color.DARK_GRAY);
                    for(int x = extraSpace; x < getWidth(); x += step) {
                        g.drawLine(x, 0, x, getHeight());
                    }
                    for(int y = extraSpace; y <= getHeight(); y += step) {
                        g.drawLine(0, y, getWidth(), y);
                    }
                }
            }
        };
        this.add(this.pixelPanel);

        this.setOpaque(true);
        this.setBackground(new Color(25, 20, 20));
        this.resizeComponents(windowDimension);
    }

    public void resizeComponents(Dimension dimension) {
        int sideLength = (int)Math.round(0.75 * Math.min(dimension.width, dimension.height));
        int remainingLength = dimension.height - sideLength;
        this.pixelPanel.setBounds(
                ((dimension.width - (sideLength / 2)) / 2) - (sideLength / 2),
                remainingLength / 4,
                sideLength, sideLength
        );
        this.updatePixelBounds();
    }

    public JPanel getLayersPanel() {
        return this.layersPanel;
    }

    public void onKeyPressed(KeyEvent e) {

    }

    public void setUpdatePortWhenRepaint(boolean updatePortWhenRepaint) {
        this.updatePortWhenRepaint = updatePortWhenRepaint;
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

        this.extraSpace = (int)Math.round((this.pixelPanel.getWidth() - (pixelLength * step)) / 2.0);
        for(GraphicPixel graphicPixel : this.graphicPixels) {
            graphicPixel.setBounds(
                    this.pixelPanel.getX() + ((graphicPixel.getPixelX() + this.finalDegree) * step) + this.extraSpace,
                    this.pixelPanel.getY() + ((graphicPixel.getPixelY() + this.finalDegree) * step) + this.extraSpace,
                    step, step
            );
        }
        this.repaint();
    }

    @Override
    public void addPixel(int index, int x, int y) {
        GraphicPixel newPixel = new GraphicPixel(this.ledControl, this, index, x, y);
        this.graphicPixels.add(newPixel);
        this.add(newPixel);
        this.setComponentZOrder(newPixel, 0);
    }

    @Override
    public Point getPixelPosition(int index) {
        GraphicPixel graphicPixel = this.graphicPixels.get(index);
        if(graphicPixel != null) {
            return new Point(graphicPixel.getPixelX(), graphicPixel.getPixelY());
        }
        else return new Point(0, 0);
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
    public void showIndexes(boolean showIndexes) {
        this.showIndexes = showIndexes;
        for(GraphicPixel graphicPixel : this.graphicPixels) {
            graphicPixel.showIndex(showIndexes);
        }
    }

    @Override
    public boolean isShowIndexes() {
        return this.showIndexes;
    }

    @Override
    public void setOrderMode(boolean orderMode, boolean submit) {
        this.orderMode = orderMode;
        if(orderMode) {
            while(!this.newOrder.isEmpty()) {
                this.newOrder.remove(0);
            }
        }
        else if(submit) {
            this.ledControl.requestNewOrder(this.newOrder);
        }
        for(GraphicPixel graphicPixel : this.graphicPixels) {
            graphicPixel.setOrderMode(orderMode);
            if(!orderMode) graphicPixel.setOrdered(false);
        }
    }

    @Override
    public void requestPixelOrdered(int oldIndex) {
        GraphicPixel graphicPixel = this.graphicPixels.get(oldIndex);
        this.newOrder.add(new ThreeCoordinatePoint(this.newOrder.size(), graphicPixel.getPixelX(), graphicPixel.getPixelY()));
        this.graphicPixels.get(oldIndex).setOrdered(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.updatePortWhenRepaint) this.ledControl.updatePort();
        for(GraphicPixel graphicPixel : this.graphicPixels) {
            if(!this.orderMode) {
                graphicPixel.setBackground(this.ledControl.getColorAt(graphicPixel.getPixelX(), graphicPixel.getPixelY()));
            } else {
                if(graphicPixel.isOrdered()) {
                    graphicPixel.setBackground(Color.GREEN);
                } else {
                    int intensity = (int)Math.round((graphicPixel.getPixelIndex() / (double)this.graphicPixels.size()) * 255);
                    graphicPixel.setBackground(new Color(intensity, intensity, intensity));
                }
            }
            graphicPixel.repaint();
        }
    }
}