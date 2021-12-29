package control.node;

import java.io.Serializable;
import java.util.Objects;

public class NodeConnection implements Serializable {

    private ThreeCoordinatePoint outputCoordinates, inputCoordinates;

    public NodeConnection(ThreeCoordinatePoint outputCoordinates, ThreeCoordinatePoint inputCoordinates) {
        this.outputCoordinates = outputCoordinates;
        this.inputCoordinates = inputCoordinates;
    }

    public ThreeCoordinatePoint getOutputCoordinates() {
        return this.outputCoordinates;
    }
    public ThreeCoordinatePoint getInputCoordinates() {
        return this.inputCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeConnection that = (NodeConnection) o;
        return outputCoordinates.equals(that.outputCoordinates) && inputCoordinates.equals(that.inputCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputCoordinates, inputCoordinates);
    }
}
