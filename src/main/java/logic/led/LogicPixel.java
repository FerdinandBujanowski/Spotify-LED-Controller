package logic.led;

import java.io.Serializable;

public class LogicPixel implements Serializable {

    public final int x, y;
    private double intensity;

    public LogicPixel(int x, int y, double intensity) {
        this.x = x;
        this.y = y;
        this.setIntensity(intensity);
    }

    public LogicPixel(int x, int y) {
        this(x, y, 0);
    }

    public double getIntensity() {
        return this.intensity;
    }

    public void setIntensity(double intensity) {
        if(intensity > 1.0) {
            this.intensity = 1.0;
        } else if(intensity < 0.0) {
            this.intensity = 0.0;
        } else {
            this.intensity = intensity;
        }
    }
}
