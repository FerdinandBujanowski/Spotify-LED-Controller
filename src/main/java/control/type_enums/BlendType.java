package control.type_enums;

public enum BlendType {

    HORIZONTAL("Horizontal"), VERTICAL("Vertical"), CIRCULAR("Circular");

    private String name;

    BlendType(String name) {
        this.name = name;
    }

    public static BlendType getTypeByString(String string) {
        for(BlendType blendType : BlendType.values()) {
            if(blendType.toString().equals(string)) {
                return blendType;
            }
        }
        return null;
    }
}
