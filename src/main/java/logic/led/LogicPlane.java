package logic.led;

import control.node.ThreeCoordinatePoint;

import java.awt.*;
import java.util.ArrayList;

public class LogicPlane {

    private LogicMask logicMask;
    private ArrayList<ThreeCoordinatePoint> colorCoordinatePoints;
    private ArrayList<Color> colors;

    public LogicPlane() {
        this.logicMask = new LogicMask();
        this.colorCoordinatePoints = new ArrayList<>();
        this.colors = new ArrayList<>();
    }

    public void setIntensityAt(int x, int y, double intensity, Color color) {
        this.logicMask.setIntensityAt(x, y, intensity);
        if(intensity == 0) return;
        int colorIndex;
        if(!this.colors.contains(color)) {
            this.colors.add(color);
        }
        colorIndex = this.colors.indexOf(color);
        this.colorCoordinatePoints.add(new ThreeCoordinatePoint(x, y, colorIndex));
    }

    public LogicMask getLogicMask() {
        return this.logicMask;
    }

    public static int mixWithIntensity(int a, int b, double intensity) {
        int mix = (int)(Math.round(a * intensity)) + (int)(Math.round(b * (1.d - intensity)));
        if(mix > 255) return 255;
        return Math.max(mix, 0);
    }

    public Color getColorAt(int x, int y) {
        for(ThreeCoordinatePoint point : this.colorCoordinatePoints) {
            if(point.getX() == x && point.getY() == y) {
                return this.colors.get(point.getZ());
            }
        }
        return Color.BLACK;
    }

    public Color[][] getColorValues() {
        int degree = this.logicMask.getDegree();
        Color[][] colors = new Color[degree][degree];
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                colors[x][y] = this.getColorAt(x, y);
            }
        }
        return colors;
    }

    public static LogicPlane multiplyMaskWithColor(LogicMask logicMask, Color color) {
        LogicPlane logicPlane = new LogicPlane();
        int degree = logicMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                logicPlane.setIntensityAt(x, y, logicMask.getIntensityAt(x, y), color);
            }
        }
        return logicPlane;
    }
}
