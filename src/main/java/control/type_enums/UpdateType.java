package control.type_enums;

public enum UpdateType {

    FLIP_ALWAYS("Flip Always", "FLA"),
    FLIP_TRUE_FALSE("Flip False->True", "FFT"),
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

    public static boolean update(boolean toggleBoolean, boolean newBoolean, UpdateType updateType) {
        switch(updateType) {
            case FLIP_ALWAYS -> {
                return (!toggleBoolean && newBoolean) || (toggleBoolean && !newBoolean);
            }
            case FLIP_TRUE_FALSE -> {
                return !toggleBoolean && newBoolean;
            }
            case WHILE_TRUE -> {
                return newBoolean;
            }
        }
        return false;
    }
}
