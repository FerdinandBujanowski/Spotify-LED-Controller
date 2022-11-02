package control.led;

import control.node.ThreeCoordinatePoint;
import control.node.TwoIntegerCorrespondence;

import java.awt.*;
import java.util.ArrayList;

public interface LedRequestAcceptor {

    void setLedGraphicUnit(LedGraphicUnit ledGraphicUnit);
    void onAddLayerRequest();
    void addPixel(int x, int y) throws Exception;
    void onDeletePixelRequest(int pixelIndex);
    void requestNewOrder(ArrayList<ThreeCoordinatePoint> newOrder);

    ArrayList<Integer> onCopyLedsRequest(ArrayList<Integer> indexes);

    void enableLayer(int layerIndex, boolean isEnabled);

    Point[] getPixels();
    Point getPixelAt(int pixelIndex);
    int getPixelIndex(int x, int y);
    void onUpdatePixelRequest(int pixelIndex, int newX, int newY, boolean deleted);
    int getFinalDegree();
    Color getColorAt(int x, int y);
    double getUsedCapacity();

    String[] onGetPortsRequest();
    void onOpenPortRequest(int index);
    void updatePort();
}
