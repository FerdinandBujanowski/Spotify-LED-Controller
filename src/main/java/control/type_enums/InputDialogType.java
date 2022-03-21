package control.type_enums;

import java.awt.*;

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

}
