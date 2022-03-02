package control.type_enums;

public enum InputDialogType {

    JOINT_TYPE_INPUT("Please enter Joint Type"),
    NUMBER_TYPE_INPUT("Please enter number"),
    INTEGER_TYPE_INPUT("Please enter integer"),
    UNIT_NUMBER_TYPE_INPUT("Please enter unit number"),
    COLOR_TYPE_INPUT("Please pick color"),
    ROUND_PIXEL_INPUT("Please select rounding algorithm"),
    ROUND_INPUT("Please select rounding algorithm");

    private String message;

    InputDialogType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
