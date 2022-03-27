package control.type_enums;

import java.awt.*;
import java.util.Objects;

public enum InputDialogType {

    JOINT_TYPE_INPUT("Please enter Joint Type"),
    NUMBER_TYPE_INPUT("Please enter number"),
    INTEGER_TYPE_INPUT("Please enter integer"),
    UNIT_NUMBER_TYPE_INPUT("Please enter unit number"),
    STRING_TYPE_INPUT("Please enter string"),
    COLOR_TYPE_INPUT("Please pick color"),
    ROUND_PIXEL_INPUT("Please select rounding algorithm"),
    ROUND_INPUT("Please select rounding algorithm");

    private final String message;

    InputDialogType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public static String valueToString(InputDialogType inputDialogType, Object value) {
        if(inputDialogType == InputDialogType.COLOR_TYPE_INPUT) {
            Color color = (Color) value;
            return String.valueOf(color.getRGB());
        } else {
            return value.toString();
        }
    }

    public static InputDialogType getTypeByString(String string) {
        for(InputDialogType inputDialogType : InputDialogType.values()) {
            if(inputDialogType.toString().equals(string)) {
                return inputDialogType;
            }
        }
        return null;
    }

    public static Object getValueByString(String inputDialogTypeString, String objectString) {
        InputDialogType inputDialogType = InputDialogType.getTypeByString(inputDialogTypeString);
        return switch (Objects.requireNonNull(inputDialogType)) {
            case NUMBER_TYPE_INPUT, UNIT_NUMBER_TYPE_INPUT -> Double.valueOf(objectString);
            case INTEGER_TYPE_INPUT -> Integer.valueOf(objectString);
            case STRING_TYPE_INPUT -> objectString;
            case JOINT_TYPE_INPUT -> JointType.getTypeByString(objectString);
            case COLOR_TYPE_INPUT -> new Color(Integer.parseInt(objectString));
            case ROUND_PIXEL_INPUT -> PixelAlgorithmType.getTypeByString(objectString);
            case ROUND_INPUT -> RoundAlgorithmType.getTypeByString(objectString);
        };
    }

}
