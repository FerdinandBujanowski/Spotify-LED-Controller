package control.type_enums;

public enum UpdateType {

    FLIP_ALWAYS("Flip Always", "FLA"),
    FLIP_TRUE_FALSE("Flip True->False", "FTF"),
    WHILE_TRUE("While True", "WHT");

    private String name, code;

    UpdateType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public static UpdateType getTypeByString(String string) {
        for(UpdateType updateType : UpdateType.values()) {
            if(updateType.toString().equals(string)) {
                return updateType;
            }
        }
        return null;
    }
}
