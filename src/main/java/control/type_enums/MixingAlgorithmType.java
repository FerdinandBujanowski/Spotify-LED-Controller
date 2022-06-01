package control.type_enums;

public enum MixingAlgorithmType {

    SUBTRACTIVE("Subtractive"), ADDITIVE("Additive"), OVERLAY("Overlay");

    private final String name;

    MixingAlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static MixingAlgorithmType getTypeByString(String string) {
        for(MixingAlgorithmType mixingAlgorithmType : MixingAlgorithmType.values()) {
            if(mixingAlgorithmType.toString().equals(string)) {
                return mixingAlgorithmType;
            }
        }
        return null;
    }
}
