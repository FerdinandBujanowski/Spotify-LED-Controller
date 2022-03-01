package control.led;

import control.SerializableFunction;
import logic.led.LogicLayer;
import logic.led.LogicMask;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

 public class LedControl implements Serializable, LedRequestAcceptor {

    ArrayList<Point> pixels;
    LogicMask pixelMask;
    ArrayList<LogicLayer> logicLayers;

    LedGraphicUnit ledGraphicUnit;

    public LedControl() {
        this.pixels = new ArrayList<>();
        this.pixelMask = new LogicMask();
        this.logicLayers = new ArrayList<>();
    }

    public int getLayerCount() {
        return this.logicLayers.size();
    }

    public SerializableFunction<Object, Integer> getUpdateMaskFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateMask;
    }

    public SerializableFunction<Color, Integer> getUpdateColorFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateColor;
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
    }

    @Override
    public void addPixelRow(int xFrom, int yFrom, int xTo, int yTo) {

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
     public int getFinalDegree() {
         return this.pixelMask.getDegree();
     }

     @Override
     public Color getColorAt(int x, int y) {
        if(this.logicLayers.size() > 0) {
            return this.logicLayers.get(this.logicLayers.size() - 1).getColorAt(x, y);
        } else return new Color(0, 0, 0);
     }
 }
