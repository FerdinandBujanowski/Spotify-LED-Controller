package control.led;

import java.awt.*;

public interface LedGraphicUnit {

    void addLayer(int newIndex);
    void updatePixelBounds();
    void updatePixelPositions();
    void addPixel(int index, int x, int y);
    Point getPixelPosition(int index);
    void movePixel(int index, int newX, int newY);
    void deletePixel(int index);
    void setDrawOnlyLedPixels(boolean drawOnlyLedPixels);
    void showIndexes(boolean showIndexes);
    boolean isShowIndexes();
    void setOrderMode(boolean orderMode, boolean submit);
    boolean canEditPixels();
    void requestPixelOrdered(int oldIndex);

    void handleSelection(int pixelIndex);
    void handleG(int x, int y);
}
