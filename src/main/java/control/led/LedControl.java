package control.led;

import control.SerializableFunction;
import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;
import control.save.LedSaveUnit;
import logic.led.LogicLayer;
import logic.led.LogicMask;
import logic.led.LogicPixel;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class LedControl implements Serializable, LedRequestAcceptor, LedNodeCommunication {

    private ArrayList<Point> pixels;
    private LogicMask pixelMask;
    private ArrayList<LogicLayer> logicLayers;

    private LedGraphicUnit ledGraphicUnit;

    private ColorSender colorSender;

    public LedControl() {
        this.pixels = new ArrayList<>();
        this.pixelMask = new LogicMask();
        this.logicLayers = new ArrayList<>();

        this.colorSender = new ColorSender("COM3");
        this.colorSender.openPort();
    }

    public void reinitialize(LedSaveUnit ledSaveUnit) {

    }
    public LedSaveUnit getLedSaveUnit() {
        return new LedSaveUnit(this.pixels, this.logicLayers.size());
    }

    public int getLayerCount() {
        return this.logicLayers.size();
    }

    @Override
    public void setLedGraphicUnit(LedGraphicUnit ledGraphicUnit) {
        this.ledGraphicUnit = ledGraphicUnit;
    }

    @Override
    public void onAddLayerRequest() {
        int layerCount = this.logicLayers.size();
        this.logicLayers.add(new LogicLayer(layerCount == 0 ? null : this.logicLayers.get(layerCount - 1)));

        this.ledGraphicUnit.addLayer(layerCount);
    }

    @Override
    public void addPixel(int x, int y) throws Exception {
        Point newPixel = new Point(x, y);
        if(!this.pixels.contains(newPixel)) {
            int newIndex = this.pixels.size();
            this.pixels.add(newPixel);
            this.pixelMask.setIntensityAt(x, y, 1.0);
            this.ledGraphicUnit.addPixel(newIndex, x, y);
        } else {
            throw new Exception("Pixel already set");
        }
        this.ledGraphicUnit.updatePixelBounds();
    }

     @Override
     public void requestNewOrder(ArrayList<ThreeCoordinatePoint> newOrder) {
        for(ThreeCoordinatePoint threeCoordinatePoint : newOrder) {
            Point oldPosition = this.pixels.get(threeCoordinatePoint.getX());
            int oldIndex = this.getPixelIndex(threeCoordinatePoint.getY(), threeCoordinatePoint.getZ());
            this.pixels.set(threeCoordinatePoint.getX(), new Point(threeCoordinatePoint.getY(), threeCoordinatePoint.getZ()));
            this.ledGraphicUnit.movePixel(threeCoordinatePoint.getX(), threeCoordinatePoint.getY(), threeCoordinatePoint.getZ());
            this.pixels.set(oldIndex, new Point(oldPosition.x, oldPosition.y));
            this.ledGraphicUnit.movePixel(oldIndex, oldPosition.x, oldPosition.y);
        }
        this.ledGraphicUnit.updatePixelBounds();
     }

     @Override
     public void enableLayer(int layerIndex, boolean isEnabled) {
        this.logicLayers.get(layerIndex).setEnabled(isEnabled);
        this.ledGraphicUnit.updatePixelBounds();
     }

     @Override
     public Point[] getPixels() {
         Point[] pixels = new Point[this.pixels.size()];
         for(int i = 0; i < pixels.length; i++) {
             pixels[i] = this.pixels.get(i);
         }
         return pixels;
     }

     @Override
     public Point getPixelAt(int pixelIndex) {
        if(this.pixels.size() <= pixelIndex) return new Point(0, 0);
        else return this.pixels.get(pixelIndex);
     }

     @Override
     public int getPixelIndex(int x, int y) {
        for(int i = 0; i < this.pixels.size(); i++) {
            Point pixel = this.pixels.get(i);
            if(pixel.x == x && pixel.y == y) {
                return i;
            }
        }
        return -1;
     }

     @Override
     public void onUpdatePixelRequest(int oldX, int oldY, int newX, int newY, boolean deleted) {
         int pixelIndex = this.getPixelIndex(oldX, oldY);
         if(pixelIndex == -1) return;

         this.pixelMask.setIntensityAt(oldX, oldY, 0);
         if(deleted) {
             this.ledGraphicUnit.deletePixel(pixelIndex);
         } else if(this.getPixelIndex(newX, newY) == -1) {
             this.pixels.set(pixelIndex, new Point(newX, newY));
             this.pixelMask.setIntensityAt(newX, newY, 1);
             this.ledGraphicUnit.movePixel(pixelIndex, newX, newY);
         }
         this.pixelMask.cleanUp();
     }

     @Override
     public int getFinalDegree() {
         return this.pixelMask.getDegree();
     }

     @Override
     public Color getColorAt(int x, int y) {
        if(this.logicLayers.size() > 0) {
            return this.logicLayers.get(this.logicLayers.size() - 1).getColorAt(x, y);
        } else return new Color(0, 0, 0);
     }

    @Override
    public void updatePort() {
        for(int i = 0; i < this.pixels.size(); i++) {
            this.colorSender.addCommand(i, this.getColorAt(this.pixels.get(i).x, this.pixels.get(i).y));
        }
        this.colorSender.flushCommands();
    }

    @Override
     public SerializableFunction<Object, Integer> updateTextureFunction(int layerIndex) {
         return this.logicLayers.get(layerIndex)::updateTexture;
     }

     @Override
     public SerializableFunction<Integer, Point> pixelPositionFunction() {
         return this::getPixelAt;
     }
 }
