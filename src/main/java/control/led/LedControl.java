package control.led;

import control.SerializableFunction;
import control.save.LedSaveUnit;
import logic.led.LogicLayer;
import logic.led.LogicMask;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

 public class LedControl implements Serializable, LedRequestAcceptor, LedNodeCommunication {

    ArrayList<Point> pixels;
    LogicMask pixelMask;
    ArrayList<LogicLayer> logicLayers;

    LedGraphicUnit ledGraphicUnit;

    public LedControl() {
        this.pixels = new ArrayList<>();
        this.pixelMask = new LogicMask();
        this.logicLayers = new ArrayList<>();
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
            this.pixels.add(newPixel);
            this.pixelMask.setIntensityAt(x, y, 1.0);
        } else {
            throw new Exception("Pixel already set");
        }
        this.ledGraphicUnit.update();
    }

     @Override
     public void enableLayer(int layerIndex, boolean isEnabled) {
        this.logicLayers.get(layerIndex).setEnabled(isEnabled);
        this.ledGraphicUnit.update();
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
     public SerializableFunction<Object, Integer> updatePlaneFunction(int layerIndex) {
         return this.logicLayers.get(layerIndex)::updatePlane;
     }

     @Override
     public SerializableFunction<Integer, Point> pixelPositionFunction() {
         return this::getPixelAt;
     }
 }
