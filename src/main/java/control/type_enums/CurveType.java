package control.type_enums;

import control.math_functions.MathFunctions;

import java.awt.*;
import java.util.function.Function;

public enum CurveType {

    CONSTANT(MathFunctions::getConstant, new Color(35, 25, 66)),
    LINEAR(MathFunctions::getLinear, new Color(94, 84, 142)),
    LOGARITHMIC(MathFunctions::getLogarithmic, new Color(159, 134, 192)),
    BEZIER(MathFunctions::getBezier, new Color(190, 149, 196)),
    EXPONENTIAL(MathFunctions::getExponential, new Color(224, 177, 203));

    private Function<Double, Double> getCurveFunction;
    private Color color;

    CurveType(Function<Double, Double> getCurveFunction, Color color) {
        this.getCurveFunction = getCurveFunction;
        this.color = color;
    }

    public double getCurve(double value) {
        if(value > 1) return 1;
        if(value < 0) return 0;

        return this.getCurveFunction.apply(value);
    }

    public Color getColor() {
        return this.color;
    }

    public static int indexOf(CurveType curveType) {
        CurveType[] curveTypes = CurveType.values();
        for(int i = 0; i < curveTypes.length; i++) {
            if(curveType.equals(curveTypes[i])) {
                return i;
            }
        }
        return -1;
    }
}
