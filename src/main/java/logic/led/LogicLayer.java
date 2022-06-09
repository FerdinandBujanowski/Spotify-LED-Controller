package logic.led;

import java.awt.*;
import java.io.Serializable;

public class LogicLayer implements Serializable {

    private LogicTexture logicTexture;
    private boolean isEnabled;

    private LogicLayer lowerLayer;

    public LogicLayer(LogicLayer lowerLayer) {
        this.lowerLayer = lowerLayer;
        this.logicTexture = new LogicTexture();
        this.isEnabled = true;
    }

    public Integer updateTexture(Object texture) {
        this.logicTexture = (LogicTexture) texture;
        return 0;
    }

    public Color getColorAt(int x, int y) {
        if(!this.isEnabled) {
            return(this.lowerLayer != null ? this.lowerLayer.getColorAt(x, y) : Color.BLACK);
        }
        double intensity = this.logicTexture.getLogicMask().getIntensityAt(x, y);
        Color lowerColor;
        if(this.lowerLayer == null) {
            lowerColor = Color.BLACK;
        } else {
            lowerColor = this.lowerLayer.getColorAt(x, y);
        }
        Color colorAt = this.logicTexture.getColorAt(x, y);
        return new Color(
                LogicTexture.mixWithIntensity(colorAt.getRed(), lowerColor.getRed(), intensity),
                LogicTexture.mixWithIntensity(colorAt.getGreen(), lowerColor.getGreen(), intensity),
                LogicTexture.mixWithIntensity(colorAt.getBlue(), lowerColor.getBlue(), intensity)
        );
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
