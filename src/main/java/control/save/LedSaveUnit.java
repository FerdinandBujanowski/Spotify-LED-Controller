package control.save;

import logic.led.LogicLayer;

import java.awt.*;
import java.util.ArrayList;

public class LedSaveUnit {

    private final ArrayList<Point> pixels;
    private final int layerSize;

    public LedSaveUnit(ArrayList<Point> pixels, int layerSize) {
        this.pixels = pixels;
        this.layerSize = layerSize;
    }

    public ArrayList<Point> getPixels() {
        return this.pixels;
    }
    public int getLayerSize() {
        return this.layerSize;
    }
}
