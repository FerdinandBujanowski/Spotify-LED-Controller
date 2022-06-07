package control.led;

public interface LedGraphicUnit {

    void addLayer(int newIndex);
    void update();
    void addPixel(int x, int y);
    void setDrawOnlyLedPixels(boolean drawOnlyLedPixels);
}
