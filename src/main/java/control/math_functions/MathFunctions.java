package control.math_functions;

public abstract class MathFunctions {

    public static double getBezier(double currentX, boolean negative) {
        if(!negative) {
            return (Math.cos(currentX * Math.PI) + 1) / 2;
        } else {
            return -(Math.cos(currentX * Math.PI) - 1) / 2;
        }
    }

    public static double getBezier(double value) {
        return 0;
    }
    public static double getConstant(double value) {
        return 1;
    }
    public static double getLinear(double value) {
        return value;
    }
    public static double getExponential(double value) {
        return 0;
    }
    public static double getLogarithmic(double value) {
        return value;
    }



}
