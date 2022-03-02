package control.type_enums;

public enum RoundAlgorithmType {

    ROUND("Round"), CEILING("Ceiling"), FLOOR("Floor");

    private final String name;

    RoundAlgorithmType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
