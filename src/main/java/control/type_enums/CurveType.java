package control.type_enums;

import control.math_functions.MathFunctions;

import java.util.function.Function;

public enum CurveType {

    BEZIER(MathFunctions::getBezier),
    CONSTANT(MathFunctions::getConstant),
    LINEAR(MathFunctions::getLinear),
    EXPONENTIAL(MathFunctions::getExponential),
    LOGARITHMIC(MathFunctions::getLogarithmic);

    private Function<Double, Double> getCurveFunction;

    CurveType(Function<Double, Double> getCurveFunction) {
        this.getCurveFunction = getCurveFunction;
    }

    public double getCurve(double value) {
        if(value > 1) return 1;
        if(value < 0) return 0;

        return this.getCurveFunction.apply(value);
    }
}
