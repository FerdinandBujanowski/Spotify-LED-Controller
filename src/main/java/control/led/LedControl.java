package control.led;

import logic.led.LogicLayer;

import java.util.ArrayList;

public class LedControl {

    ArrayList<LogicLayer> logicLayers;

    public LedControl() {
        this.logicLayers = new ArrayList<>();
    }

    public void addLayer() {
        int layerCount = this.logicLayers.size();
        this.logicLayers.add(new LogicLayer(layerCount == 0 ? null : this.logicLayers.get(layerCount - 1)));
    }
}
