package control.led;

import control.SerializableFunction;
import logic.led.LogicLayer;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class LedControl implements Serializable {

    ArrayList<Point> pixels;
    ArrayList<LogicLayer> logicLayers;

    public LedControl() {
        this.pixels = new ArrayList<>();
        this.logicLayers = new ArrayList<>();
    }

    public int getLayerCount() {
        return this.logicLayers.size();
    }

    public void addLayer() {
        int layerCount = this.logicLayers.size();
        this.logicLayers.add(new LogicLayer(layerCount == 0 ? null : this.logicLayers.get(layerCount - 1)));
    }

    public SerializableFunction<Object, Integer> getUpdateMaskFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateMask;
    }

    public SerializableFunction<Color, Integer> getUpdateColorFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateColor;
    }
}
