package control.led;

public interface LedGraphicUnit {

    void addLayer(int newIndex);
    void update();
    void addPixel(int x, int y);
    void movePixel(int index, int newX, int newY);
    void deletePixel(int index);
    void setDrawOnlyLedPixels(boolean drawOnlyLedPixels);
}
