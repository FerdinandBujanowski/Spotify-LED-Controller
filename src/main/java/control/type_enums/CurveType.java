package control.type_enums;

import control.math_functions.MathFunctions;

import java.awt.*;
import java.util.function.Function;

public enum CurveType {

    CONSTANT(MathFunctions::getConstant, new Color(35, 25, 66)),
    LINEAR(MathFunctions::getLinear, new Color(94, 84, 142)),
    LINEAR_INVERSE(MathFunctions::getLinearInverse, new Color(0, 0, 0)),
    QUADRATIC(MathFunctions::getQuadratic, new Color(0, 0, 0)),
    QUADRATIC_INVERSE(MathFunctions::getQuadraticInverse, new Color(0, 0, 0)),
    CENTER_PEAK(MathFunctions::getCenterPeak, new Color(0, 0, 0)),
    CENTER_PEAK_INVERSE(MathFunctions::getCenterPeakInverse, new Color(0, 0, 0)),
    COSINE(MathFunctions::getCosine, new Color(0, 0, 0)),
    COSINE_INVERSE(MathFunctions::getCosineInverse, new Color(0, 0, 0));

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
