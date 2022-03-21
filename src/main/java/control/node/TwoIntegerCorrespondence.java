package control.node;

import java.awt.*;
import java.util.ArrayList;

public class TwoIntegerCorrespondence {

    private final ArrayList<Point> correspondence;

    public TwoIntegerCorrespondence() {
        this.correspondence = new ArrayList<>();
    }

    public void addValue(int a, int b) {
        this.correspondence.add(new Point(a, b));
    }

    public int getCorrespondingValue(int a) {
        for(Point point : this.correspondence) {
            if(point.x == a) {
                return point.y;
            }
        }
        return -1;
    }
}
