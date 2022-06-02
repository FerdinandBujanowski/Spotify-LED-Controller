package control.led;

import control.SerializableFunction;

import java.awt.*;

public interface LedNodeCommunication {

    SerializableFunction<Object, Integer> updatePlaneFunction(int layerIndex);
    SerializableFunction<Integer, Point> pixelPositionFunction();

}
