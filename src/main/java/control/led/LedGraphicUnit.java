package control.led;

public interface LedGraphicUnit {

    void addLayer(int newIndex);
    void updatePixelBounds();
    void updatePixelPositions();
    void addPixel(int index, int x, int y);
    void movePixel(int index, int newX, int newY);
    void deletePixel(int index);
    void setDrawOnlyLedPixels(boolean drawOnlyLedPixels);
    void showIndexes(boolean showIndexes);
    boolean isShowIndexes();
}
