package control.node;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
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

    public boolean containsYZ(Point point) {
        return this.getY() == point.x && this.getZ() == point.y;
    }

    public static boolean listContainsYZ(ArrayList<ThreeCoordinatePoint> list, Point point) {
        for(ThreeCoordinatePoint threeCoordinatePoint : list) {
            if(threeCoordinatePoint.containsYZ(point)) return true;
        }
        return false;
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
