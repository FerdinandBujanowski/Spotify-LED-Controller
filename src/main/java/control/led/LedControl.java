package control.led;

import logic.led.LogicLayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;

public class LedControl {

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

    public Function<Object, Integer> getUpdateMaskFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateMask;
    }

    public Function<Color, Integer> getUpdateColorFunctionForLayer(int layerIndex) {
        return this.logicLayers.get(layerIndex)::updateColor;
    }
}
