package control.type_enums;

public enum AxisType {

    HORIZONTAL("Horizontal"), VERTICAL("Vertical"), CIRCULAR("Circular");

    private String name;

    AxisType(String name) {
        this.name = name;
    }

    public static AxisType getTypeByString(String string) {
        for(AxisType axisType : AxisType.values()) {
            if(axisType.toString().equals(string)) {
                return axisType;
            }
        }
        return null;
    }
}
