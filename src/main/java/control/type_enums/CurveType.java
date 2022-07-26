package control.type_enums;

import control.math_functions.MathFunctions;
import gui.Dialogues;

import java.awt.*;
import java.util.function.Function;

public enum CurveType {

    CONSTANT(MathFunctions::getConstant, new Color(35, 25, 66)),
    LINEAR(MathFunctions::getLinear, new Color(94, 84, 142)),
    LINEAR_INVERSE(MathFunctions::getLinearInverse, new Color(94, 84, 142)),
    QUADRATIC(MathFunctions::getQuadratic, new Color(159, 134, 192)),
    QUADRATIC_INVERSE(MathFunctions::getQuadraticInverse, new Color(159, 134, 192)),
    CENTER_PEAK(MathFunctions::getCenterPeak, new Color(190, 149, 196)),
    CENTER_PEAK_INVERSE(MathFunctions::getCenterPeakInverse, new Color(190, 149, 196)),
    COSINE(MathFunctions::getCosine, new Color(224, 177, 203)),
    COSINE_INVERSE(MathFunctions::getCosineInverse, new Color(224, 177, 203)),
    USER_INPUT(MathFunctions::getLinear, Color.GRAY);

    private final Function<Double, Double> getCurveFunction;
    private final Color color;

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

    public static CurveType getCurveTypeByString(String name) {
        for(CurveType curveType : CurveType.values()) {
            if(curveType.toString().equals(name)) {
                return curveType;
            }
        }
        return null;
    }
}
