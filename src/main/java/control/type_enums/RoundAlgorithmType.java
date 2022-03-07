package control.type_enums;

public enum RoundAlgorithmType {

    ROUND("Round"), CEILING("Ceiling"), FLOOR("Floor");

    private final String name;

    RoundAlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
