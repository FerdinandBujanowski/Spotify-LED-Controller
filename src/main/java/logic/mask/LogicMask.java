package logic.mask;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;

public class LogicMask {

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
            if(pixel.x > Math.abs(degree)) degree = Math.abs(degree);
            if(pixel.y > Math.abs(degree)) degree = Math.abs(pixel.y);
        }
        return degree;
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

    private static LogicMask getJoinedMask(LogicMask maskA, LogicMask maskB, Function<Double[], Double> operation) {
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
}
