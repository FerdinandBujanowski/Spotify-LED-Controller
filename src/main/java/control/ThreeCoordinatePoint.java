package control;

import java.io.Serializable;
import java.util.Objects;

public class ThreeCoordinatePoint implements Serializable {

    private int x, y, z;

    public ThreeCoordinatePoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public ThreeCoordinatePoint() {
        this(0, 0, 0);
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreeCoordinatePoint that = (ThreeCoordinatePoint) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
