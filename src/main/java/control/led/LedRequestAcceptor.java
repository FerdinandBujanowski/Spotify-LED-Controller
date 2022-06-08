package control.led;

import java.awt.*;
import java.util.ArrayList;

public interface LedRequestAcceptor {

    void setLedGraphicUnit(LedGraphicUnit ledGraphicUnit);
    void onAddLayerRequest();
    void addPixel(int x, int y) throws Exception;

    void enableLayer(int layerIndex, boolean isEnabled);

    Point[] getPixels();
    Point getPixelAt(int pixelIndex);
    int getPixelIndex(int x, int y);
    void onUpdatePixelRequest(int oldX, int oldY, int newX, int newY, boolean deleted);
    int getFinalDegree();
    Color getColorAt(int x, int y);
}
