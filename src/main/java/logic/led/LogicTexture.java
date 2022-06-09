package logic.led;

import control.node.ThreeCoordinatePoint;

import java.awt.*;
import java.util.ArrayList;

public class LogicTexture {

    private LogicMask logicMask;
    private ArrayList<ThreeCoordinatePoint> colorCoordinatePoints;
    private ArrayList<Color> colors;

    public LogicTexture() {
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

    public static Color addColors(Color a, Color b) {
        return new Color(
                Math.min(a.getRed() + b.getRed(), 255),
                Math.min(a.getGreen() + b.getGreen(), 255),
                Math.min(a.getBlue() + b.getBlue(), 255)
        );
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
        this.logicMask.cleanUp();
        int degree = this.logicMask.getDegree();
        Color[][] colors = new Color[(degree * 2) + 1][(degree * 2) + 1];
        for(int x = 0; x < colors.length; x++) {
            for(int y = 0; y < colors.length; y++) {
                colors[x][y] = this.getColorAt(x - degree, y - degree);
            }
        }
        return colors;
    }

    public static LogicTexture multiplyMaskWithColor(LogicMask logicMask, Color color) {

        LogicTexture logicTexture = new LogicTexture();
        int degree = logicMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                logicTexture.setIntensityAt(x, y, logicMask.getIntensityAt(x, y), color);
            }
        }
        logicTexture.getLogicMask().cleanUp();
        return logicTexture;
    }

    public static LogicTexture getTextureAdditive(LogicTexture textureOver, LogicTexture textureUnder) {
        LogicMask outputMask = LogicMask.getJoinedMask_Add(textureOver.logicMask, textureUnder.logicMask);
        LogicTexture logicTexture = new LogicTexture();
        int degree = outputMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                Color colorOver = textureOver.getColorAt(x, y);
                Color colorUnder = textureUnder.getColorAt(x, y);
                Color mixedColor = LogicTexture.addColors(colorOver, colorUnder);
                logicTexture.setIntensityAt(x, y, outputMask.getIntensityAt(x, y), mixedColor);
            }
        }

        return logicTexture;
    }

    public static LogicTexture getTextureOverlay(LogicTexture textureOver, LogicTexture textureUnder) {
        LogicMask outputMask = LogicMask.getJoinedMask_Add(textureOver.logicMask, textureUnder.logicMask);
        LogicTexture logicTexture = new LogicTexture();
        int degree = outputMask.getDegree();
        for(int x = -degree; x <= degree; x++) {
            for(int y = -degree; y <= degree; y++) {
                Color colorOver = textureOver.getColorAt(x, y), colorUnder = textureUnder.getColorAt(x, y);
                double intensity = textureOver.logicMask.getIntensityAt(x, y);
                Color mixedColor = new Color(
                        LogicTexture.mixWithIntensity(colorOver.getRed(), colorUnder.getRed(), intensity),
                        LogicTexture.mixWithIntensity(colorOver.getGreen(), colorUnder.getGreen(), intensity),
                        LogicTexture.mixWithIntensity(colorOver.getBlue(), colorUnder.getBlue(), intensity)
                );
                logicTexture.setIntensityAt(x, y, outputMask.getIntensityAt(x, y), mixedColor);
            }
        }
        return logicTexture;
    }
}
