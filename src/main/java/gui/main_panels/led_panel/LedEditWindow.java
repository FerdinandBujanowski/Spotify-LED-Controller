package gui.main_panels.led_panel;

import control.led.*;
import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;
import gui.main_panels.node_panel.GraphicNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LedEditWindow extends JPanel implements LedGraphicUnit {

    private LedRequestAcceptor ledControl;

    private int finalDegree;
    private int extraSpace;
    private boolean drawOnlyLedPixels;
    private boolean showIndexes;
    private boolean orderMode;
    private boolean updatePortWhenRepaint;

    ArrayList<ThreeCoordinatePoint> newOrder;

    private ArrayList<Integer> selectedPixelIndexes, copiedPixelIndexes;
    private boolean toggleShift, toggleCtrl, toggleG;
    private boolean initiateToggleG;
    private Point movingOffset;

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

        this.newOrder = new ArrayList<>();

        this.selectedPixelIndexes = new ArrayList<>();
        this.copiedPixelIndexes = new ArrayList<>();
        this.movingOffset = new Point();

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

        LedEditWindow that = this;
        this.pixelPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    Point clickPosition = getClosestCoordinates(pixelPanel.getX() + e.getX(), pixelPanel.getY() + e.getY());
                    try {
                        ledControl.addPixel(clickPosition.x, clickPosition.y);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                that.getMouseListeners()[0].mousePressed(e);
            }
        });
        this.pixelPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleG(pixelPanel.getX() + e.getX(), pixelPanel.getY() + e.getY());
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toggleG = false;
                removeWholeSelection();
                requestFocus();
                updatePixelBounds();
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleG(e.getX(), e.getY());
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
        switch(e.getKeyCode()) {
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
                if(!this.toggleG) this.updatePixelBounds();
            }
            case KeyEvent.VK_C -> {
                if(this.toggleCtrl) {
                    this.copiedPixelIndexes.removeAll(this.copiedPixelIndexes);
                    this.copiedPixelIndexes.addAll(this.selectedPixelIndexes);
                }
            }
            case KeyEvent.VK_V -> {
                if(this.toggleCtrl) {
                    ArrayList<Integer> newIndexes = this.ledControl.onCopyLedsRequest(this.selectedPixelIndexes);
                    this.removeWholeSelection();
                    for(int index : newIndexes) {
                        this.graphicPixels.get(index).setInSelection(true);
                        this.selectedPixelIndexes.add(index);
                    }
                    this.toggleG = true;
                    this.initiateToggleG = true;
                }
            }
            case KeyEvent.VK_A -> {
                if(this.toggleCtrl) {
                    this.removeWholeSelection();
                    for(int i = 0; i < this.graphicPixels.size(); i++) {
                        this.selectedPixelIndexes.add(i);
                        this.graphicPixels.get(i).setInSelection(true);
                    }
                }
                repaint();
            }
        }
    }

    public void onKeyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.toggleShift = false;
            repaint();
        }
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.toggleCtrl = false;
            this.repaint();
        }
    }

    public void setUpdatePortWhenRepaint(boolean updatePortWhenRepaint) {
        this.updatePortWhenRepaint = updatePortWhenRepaint;
    }

    private Point getClosestCoordinates(int x, int y) {
        int pixelLength = (this.finalDegree * 2) + 1;
        int step = (int)Math.round(this.pixelPanel.getWidth() / (double)pixelLength);

        int xInPanel = (x - this.pixelPanel.getX() - this.extraSpace);
        int yInPanel = (y - this.pixelPanel.getY() - this.extraSpace);

        int pixelX = (xInPanel - (xInPanel % step)) / step - finalDegree;
        if(xInPanel < 0) pixelX--;
        int pixelY = (yInPanel - (yInPanel % step)) / step - finalDegree;
        if(yInPanel < 0) pixelY--;

        return new Point(pixelX, pixelY);
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
        if(this.toggleShift) {
            ThreeCoordinatePoint lastOrderedPixel = this.newOrder.get(this.newOrder.size() - 1);
            GraphicPixel lastPixel = this.graphicPixels.get(oldIndex);

            System.out.println(lastOrderedPixel.getY() + " -> " + lastPixel.getPixelX() + ", " + lastOrderedPixel.getZ() + " -> " + lastPixel.getPixelY());
            Point from = new Point(lastOrderedPixel.getY(), lastOrderedPixel.getZ());
            Point to = new Point(lastPixel.getPixelX(), lastPixel.getPixelY());
            for(int y = Math.min(from.y, to.y); y <= Math.max(from.y, to.y); y++) {
                for(int x = Math.min(from.x, to.x); x <= Math.max(from.x, to.x); x++) {
                    int pixelIndex = this.ledControl.getPixelIndex(x, y);
                    if(pixelIndex != -1) {
                        GraphicPixel currentGraphicPixel = this.graphicPixels.get(pixelIndex);
                        if(!currentGraphicPixel.isOrdered()) {
                            this.newOrder.add(new ThreeCoordinatePoint(this.newOrder.size(), x, y));
                            currentGraphicPixel.setOrdered(true);
                        }
                    }
                }
            }
        } else {
            GraphicPixel graphicPixel = this.graphicPixels.get(oldIndex);
            this.newOrder.add(new ThreeCoordinatePoint(this.newOrder.size(), graphicPixel.getPixelX(), graphicPixel.getPixelY()));
            this.graphicPixels.get(oldIndex).setOrdered(true);
        }
    }

    @Override
    public void handleSelection(int pixelIndex) {
        if(this.graphicPixels.get(pixelIndex) == null) return;

        if(this.toggleShift) {
            if(!this.selectedPixelIndexes.contains(pixelIndex)) {
                this.selectedPixelIndexes.add(pixelIndex);
                this.graphicPixels.get(pixelIndex).setInSelection(true);
            }
        } else if(this.toggleCtrl) {
            this.selectedPixelIndexes.remove((Integer) pixelIndex);
            this.graphicPixels.get(pixelIndex).setInSelection(false);
        } else {
            this.removeWholeSelection();
            this.selectedPixelIndexes.add(pixelIndex);
            this.graphicPixels.get(pixelIndex).setInSelection(true);
        }
        repaint();
    }

    @Override
    public void handleG(int x, int y) {
        if(!this.toggleG) return;

        if(this.initiateToggleG) {
            this.initiateToggleG = false;
            this.movingOffset = this.getClosestCoordinates(x, y);
        }
        Point closestCoordinates = this.getClosestCoordinates(x, y);
        Point relativeMovement = new Point(closestCoordinates.x - this.movingOffset.x, closestCoordinates.y - this.movingOffset.y);

        if(relativeMovement.x == 0 && relativeMovement.y == 0) return;

        for(int index : this.selectedPixelIndexes) {
            GraphicPixel currentPixel = this.graphicPixels.get(index);
            if(currentPixel != null) {
                this.ledControl.onUpdatePixelRequest(index, currentPixel.getPixelX() + relativeMovement.x, currentPixel.getPixelY() + relativeMovement.y, false);
            }
        }
        this.movingOffset.setLocation(this.movingOffset.x + relativeMovement.x, this.movingOffset.y + relativeMovement.y);
        repaint();
    }

    private void removeWholeSelection() {
        while(!this.selectedPixelIndexes.isEmpty()) {
            this.graphicPixels.get(this.selectedPixelIndexes.get(0)).setInSelection(false);
            this.selectedPixelIndexes.remove(0);
        }
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