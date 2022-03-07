package control.type_enums;

import java.awt.*;

public enum InputDialogType {

    JOINT_TYPE_INPUT("Please enter Joint Type", JointType.class),
    NUMBER_TYPE_INPUT("Please enter number", Double.class),
    INTEGER_TYPE_INPUT("Please enter integer", Integer.class),
    UNIT_NUMBER_TYPE_INPUT("Please enter unit number", Double.class),
    COLOR_TYPE_INPUT("Please pick color", Color.class),
    ROUND_PIXEL_INPUT("Please select rounding algorithm", PixelAlgorithmType.class),
    ROUND_INPUT("Please select rounding algorithm", RoundAlgorithmType.class);

    private final String message;
    private final Class inputClass;

    InputDialogType(String message, Class inputClass) {
        this.message = message;
        this.inputClass = inputClass;
    }

    public String getMessage() {
        return this.message;
    }
    public Class getInputClass() {
        return this.inputClass;
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
