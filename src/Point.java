import java.io.Serializable;

/**
 * Created by dima on 11/06/16.
 */
public class Point implements Serializable {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public double getDistanceTo(Point p) {
        return Math.sqrt(Math.pow(x - p.x, 2) +
                         Math.pow(y - p.y, 2));
    }
}
