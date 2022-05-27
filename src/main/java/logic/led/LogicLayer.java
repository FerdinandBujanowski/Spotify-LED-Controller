package logic.led;

import java.awt.*;
import java.io.Serializable;

public class LogicLayer implements Serializable {

    private LogicPlane logicPlane;
    private boolean isEnabled;

    private LogicLayer lowerLayer;

    public LogicLayer(LogicLayer lowerLayer) {
        this.lowerLayer = lowerLayer;
        this.logicPlane = new LogicPlane();
        this.isEnabled = true;
    }

    public Integer updatePlane(Object plane) {
        this.logicPlane = (LogicPlane) plane;
        return 0;
    }

    public Color getColorAt(int x, int y) {
        if(!this.isEnabled) {
            return(this.lowerLayer != null ? this.lowerLayer.getColorAt(x, y) : Color.BLACK);
        }
        double intensity = this.logicPlane.getLogicMask().getIntensityAt(x, y);
        Color lowerColor;
        if(this.lowerLayer == null) {
            lowerColor = Color.BLACK;
        } else {
            lowerColor = this.lowerLayer.getColorAt(x, y);
        }
        Color colorAt = this.logicPlane.getColorAt(x, y);
        return new Color(
                LogicPlane.mixWithIntensity(colorAt.getRed(), lowerColor.getRed(), intensity),
                LogicPlane.mixWithIntensity(colorAt.getGreen(), lowerColor.getGreen(), intensity),
                LogicPlane.mixWithIntensity(colorAt.getBlue(), lowerColor.getBlue(), intensity)
        );
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
