package control.type_enums;

public enum PixelAlgorithmType {

    CLOSEST_NEIGHBOR("Closest Neighbor"), LINEAR_INTERPOLATION("Linear Interpolation");

    private final String name;

    PixelAlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
