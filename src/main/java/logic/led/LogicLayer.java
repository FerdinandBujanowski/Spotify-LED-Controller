package logic.led;

import java.awt.*;
import java.io.Serializable;

public class LogicLayer implements Serializable {

    private LogicMask logicMask;
    private Color color;

    private LogicLayer lowerLayer;

    public LogicLayer(LogicLayer lowerLayer) {
        this.lowerLayer = lowerLayer;
        this.logicMask = new LogicMask();
        this.color = Color.BLACK;
    }

    public Integer updateMask(Object mask) {
        this.logicMask = (LogicMask) mask;
        return 0;
    }

    public Integer updateColor(Color color) {
        this.color = color;
        return 0;
    }

    public Color getColorAt(int x, int y) {
        if(this.lowerLayer == null) {
            return this.color;
        } else {
            Color lowerColor = this.lowerLayer.getColorAt(x, y);
            double intensity = this.logicMask.getIntensityAt(x, y);
            return new Color(
                    this.mixWithIntensity(this.color.getRed(), lowerColor.getRed(), intensity),
                    this.mixWithIntensity(this.color.getGreen(), lowerColor.getGreen(), intensity),
                    this.mixWithIntensity(this.color.getBlue(), lowerColor.getBlue(), intensity)
            );
        }
    }

    private int mixWithIntensity(int a, int b, double intensity) {
        return (int)(Math.round(a * intensity)) + (int)(Math.round(b * (1.d - intensity)));
    }
}
