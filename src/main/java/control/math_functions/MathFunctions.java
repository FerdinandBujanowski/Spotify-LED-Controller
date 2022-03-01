package control.math_functions;

import java.io.Serializable;

public abstract class MathFunctions implements Serializable {

    public static double getBezier(double currentX, boolean negative) {
        if(!negative) {
            return (Math.cos(currentX * Math.PI) + 1) / 2;
        } else {
            return -(Math.cos(currentX * Math.PI) - 1) / 2;
        }
    }

    public static double getCosine(double value) {
        return (- Math.cos(value * Math.PI) + 1) / 2;
    }
    public static double getCosineInverse(double value) {
        return (Math.cos(value * Math.PI) + 1) / 2;
    }
    public static double getConstant(double value) {
        return 1;
    }
    public static double getLinear(double value) {
        return value;
    }
    public static double getLinearInverse(double value) {
        return 1 - value;
    }
    public static double getQuadratic(double value) {
        return Math.pow(value, 2);
    }
    public static double getQuadraticInverse(double value) {
        return -Math.pow(value, 2) + 1;
    }
    public static double getCenterPeak(double value) {
        return -4 * Math.pow((value - 0.5d), 2) + 1;
    }
    public static double getCenterPeakInverse(double value) {
        return 4 * Math.pow((value - 0.5d), 2);
    }
}
