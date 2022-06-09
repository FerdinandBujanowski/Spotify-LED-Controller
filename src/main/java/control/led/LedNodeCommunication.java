package control.led;

import control.SerializableFunction;

import java.awt.*;

public interface LedNodeCommunication {

    SerializableFunction<Object, Integer> updateTextureFunction(int layerIndex);
    SerializableFunction<Integer, Point> pixelPositionFunction();

}
