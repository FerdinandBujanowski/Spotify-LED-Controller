package control.math_functions;

public abstract class MathFunctions {

    public static double getBezier(double currentX, boolean negative) {
        if(!negative) {
            return (Math.cos(currentX * Math.PI) + 1) / 2;
        } else {
            return -(Math.cos(currentX * Math.PI) - 1) / 2;
        }
    }

}
