package control.led;

import java.awt.*;
import java.util.ArrayList;

public interface LedRequestAcceptor {

    void setLedGraphicUnit(LedGraphicUnit ledGraphicUnit);
    void onAddLayerRequest();
    void addPixel(int x, int y) throws Exception;

    void enableLayer(int layerIndex, boolean isEnabled);

    Point[] getPixels();
    int getFinalDegree();
    Color getColorAt(int x, int y);
}
