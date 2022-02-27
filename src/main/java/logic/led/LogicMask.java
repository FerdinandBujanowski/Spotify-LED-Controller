package logic.led;

import control.SerializableFunction;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Function;

public class LogicMask implements Serializable {

    public ArrayList<LogicPixel> pixels;

    public LogicMask() {
        this.pixels = new ArrayList<>();
    }

    public void setIntensityAt(int x, int y, double intensity) {
        for(LogicPixel pixel : this.pixels) {
            if(pixel.x == x && pixel.y == y) {
                pixel.setIntensity(intensity);
                return;
            }
        }
        this.pixels.add(new LogicPixel(x, y, intensity));
    }

    public double getIntensityAt(int x, int y) {
        for(LogicPixel pixel : this.pixels) {
            if(pixel.x == x && pixel.y == y) {
                return pixel.getIntensity();
            }
        }
        return 0;
    }

    public void cleanUp() {
        this.pixels.removeIf(logicPixel -> logicPixel.getIntensity() == 0);
    }

    public Point[] getCoordinates() {
        this.cleanUp();
        Point[] coordinates = new Point[this.pixels.size()];
        for(int i = 0; i < coordinates.length; i++) {
            coordinates[i] = new Point(this.pixels.get(i).x, this.pixels.get(i).y);
        }
        return coordinates;
    }

    public int getDegree() {
        int degree = 0;
        for(LogicPixel pixel : this.pixels) {
            if(pixel.x > Math.abs(degree)) degree = Math.abs(pixel.x);
            if(pixel.y > Math.abs(degree)) degree = Math.abs(pixel.y);
        }
        return degree;
    }

    public Double[][] getValues() {
        this.cleanUp();
        int degree = this.getDegree();
        Double[][] values = new Double[(degree * 2) + 1][(degree * 2) + 1];
        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values.length; j++) {
                values[i][j] = this.getIntensityAt(i - degree, j - degree);
            }
        }
        return values;
    }

    @Override
    public String toString() {
        int degree = this.getDegree();
        String output = "";
        for(int i = -degree; i <= degree; i++) {
            for(int j = -degree; j <= degree; j++) {
                output += this.getIntensityAt(i, j) + " ";
            }
            output += "\n";
        }
        return output;
    }

    private static LogicMask getJoinedMask(LogicMask maskA, LogicMask maskB, SerializableFunction<Double[], Double> operation) {
        int degA = maskA.getDegree();
        int degB = maskB.getDegree();
        int degree = (Math.max(degA, degB));

        LogicMask joinedMask = new LogicMask();
        for(int i = -degree; i <= degree; i++) {
            for(int j = -degree; j <= degree; j++) {
                double newIntensity = operation.apply(
                        new Double[] {
                                maskA.getIntensityAt(i, j),
                                maskB.getIntensityAt(i, j)
                        }
                );
                if(newIntensity > 0) {
                    joinedMask.setIntensityAt(i, j, newIntensity);
                }
            }
        }
        joinedMask.cleanUp();
        return joinedMask;
    }

    public static LogicMask getJoinedMask_Add(LogicMask maskA, LogicMask maskB) {
        return LogicMask.getJoinedMask(
                maskA,
                maskB,
                doubles -> doubles[0] + doubles[1]
        );
    }

    public static LogicMask getJoinedMask_Subtract(LogicMask maskA, LogicMask maskB) {
        return LogicMask.getJoinedMask(
                maskA,
                maskB,
                doubles -> doubles[0] - doubles[1]
        );
    }

    public static LogicMask getJoinedMask_Union(LogicMask maskA, LogicMask maskB) {
        return LogicMask.getJoinedMask(
                maskA,
                maskB,
                doubles -> {
                    if(doubles[0] > 0 && doubles[1] > 0) {
                        return doubles[0] + doubles[1];
                    } else {
                        return 0.0;
                    }
                }
        );
    }

    public static LogicMask getJoinedMask_Difference(LogicMask maskA, LogicMask maskB) {
        return LogicMask.getJoinedMask(
                maskA,
                maskB,
                doubles -> {
                    if(doubles[0] > 0 && doubles[1] <= 0) {
                        return doubles[0];
                    } else if(doubles[0] <= 0 && doubles[1] > 0) {
                        return doubles[1];
                    } else {
                        return 0.0;
                    }
                }
        );
    }

    public static LogicMask getJoinedMask_Multiply(LogicMask maskA, LogicMask maskB) {
        return LogicMask.getJoinedMask(
                maskA,
                maskB,
                doubles -> doubles[0] * doubles[1]
        );
    }

    public static LogicMask getInvertedMask(LogicMask mask, int degree) {
        LogicMask newMask = new LogicMask();
        int newDegree = (Math.max(degree, mask.getDegree()));

        for(int i = -newDegree; i <= newDegree; i++) {
            for(int j = -newDegree; j <= newDegree; j++) {
                newMask.setIntensityAt(i, j, 1.0 - mask.getIntensityAt(i, j));
            }
        }
        return newMask;
    }

    public static LogicMask getMultipliedMask(LogicMask mask, double scalar) {
        LogicMask newMask = new LogicMask();
        int degree = mask.getDegree();
        for(int i = -degree; i <= degree; i++) {
            for(int j = -degree; j <= degree; j++) {
                newMask.setIntensityAt(i, j, scalar * mask.getIntensityAt(i, j));
            }
        }
        return newMask;
    }

    public static LogicMask getMovedMask(LogicMask mask, Point movement) {
        LogicMask newMask = new LogicMask();
        for(Point pixel : mask.getCoordinates()) {
            newMask.setIntensityAt(pixel.x + movement.x, pixel.y + movement.y, mask.getIntensityAt(pixel.x, pixel.y));
        }
        newMask.cleanUp();
        return newMask;
    }

    public static LogicMask getScaledMask_Closest(LogicMask mask, double scaleX, double scaleY) {
        if(scaleX == 0) scaleX = 1.d;
        if(scaleY == 0) scaleY = 1.d;

        LogicMask newMask = new LogicMask();
        int degreeX = (int)Math.round(mask.getDegree() * scaleX);
        int degreeY = (int)Math.round(mask.getDegree() * scaleY);
        for(int i = -degreeX; i <= degreeX; i++) {
            for(int j = -degreeY; j <= degreeY; j++) {
                int oldX = (int)Math.round(i / scaleX);
                int oldY = (int)Math.round(j / scaleY);
                newMask.setIntensityAt(i, j, mask.getIntensityAt(oldX, oldY));
            }
        }
        return newMask;
    }

    public static LogicMask getScaledMask_Linear(LogicMask mask, double scaleX, double scaleY) {
        if(scaleX == 0) scaleX = 1.d;
        if(scaleY == 0) scaleY = 1.d;

        LogicMask newMask = new LogicMask();
        int degreeX = (int)Math.round(mask.getDegree() * scaleX);
        int degreeY = (int)Math.round(mask.getDegree() * scaleY);
        for(int i = -degreeX; i <= degreeX; i++) {
            for(int j = -degreeY; j <= degreeY; j++) {

                double oldX_d = i / scaleX;
                int oldX_high = (int)Math.ceil(oldX_d);
                int oldX_low = (int)Math.floor(oldX_d);
                double oldY_d = i / scaleY;
                int oldY_high = (int)Math.ceil(oldY_d);
                int oldY_low = (int)Math.floor(oldY_d);

                double intensityX_A = LogicMask.linearInterpolation(
                        mask.getIntensityAt(oldX_high, oldY_high),
                        mask.getIntensityAt(oldX_low, oldY_high),
                        oldX_high - oldX_d
                );
                double intensityX_B = LogicMask.linearInterpolation(
                        mask.getIntensityAt(oldX_high, oldY_low),
                        mask.getIntensityAt(oldX_low, oldY_low),
                        oldX_high - oldX_d
                );
                double finalIntensity = LogicMask.linearInterpolation(
                        intensityX_A,
                        intensityX_B,
                        oldY_high - oldY_d
                );

                newMask.setIntensityAt(i, j, finalIntensity);
            }
        }
        return newMask;
    }

    private static double linearInterpolation(double valueA, double valueB, double interpolation) {
        if(interpolation > 1) interpolation = 1.d;
        if(interpolation < 0) interpolation = 0.d;

        return (valueA * interpolation) + (valueB * (1.d - interpolation));
    }

    public static LogicMask getRotatedMask_Closest(LogicMask mask, double radians) {
        int degree = mask.getDegree();
        double[] rotatedDegreeCoordinates = LogicMask.rotateCoordinates(degree, degree, radians);
        int rotatedDegree = (int)Math.round(Math.max(rotatedDegreeCoordinates[0], rotatedDegreeCoordinates[1]));

        LogicMask newMask = new LogicMask();
        for(int i = -rotatedDegree; i <= rotatedDegree; i++) {
            for(int j = -rotatedDegree; j <= rotatedDegree; j++) {
                double[] rotatedCoordinates = LogicMask.rotateCoordinates(i, j, -radians);
                Point closestOldCoordinates = new Point(
                        (int)Math.round(rotatedCoordinates[0]),
                        (int)Math.round(rotatedCoordinates[1])
                );
                newMask.setIntensityAt(i, j, mask.getIntensityAt(closestOldCoordinates.x, closestOldCoordinates.y));
            }
        }
        newMask.cleanUp();
        return newMask;
    }

    private static double[] rotateCoordinates(double x, double y, double radians) {
        return new double[] {
                Math.cos(radians) * x - Math.sin(radians) * y,
                Math.sin(radians) * x + Math.cos(radians) * y
        };
    }

}
