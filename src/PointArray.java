import java.util.ArrayList;
import java.awt.Point;

/**
 * Created by dima on 28/05/16.
 */
public class PointArray {
    private ArrayList<Point> points;
    public int active;

    public PointArray () {
        points = new ArrayList<Point>();
        active = -1;
    }

    public void add(Point p) {
        points.add(p);
    }

    public void add(int idx, Point p) {
        points.add(idx, p);
    }

    public void copyFrom (PointArray newPoints) {
        for (Point p: newPoints.points) points.add(p);
    }

    public void addPointArray(PointArray pa) {
        for (int i = 0; i < pa.size(); i++) points.add(pa.get(i));
    }

    // searches index of a point in the array
    public int getIndex(Point p) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x == p.x && points.get(i).y == p.y) return i;
        }

        return -1;
    }

    // searches nearest point
    public int getNearest(Point p) {
        int res = 0;
        for (int i = 0; i < points.size(); i++) {

        }
        return res;
    }

    // searches nearest point in given distance
    public int getInDistance(Point p, int distance) {
        int res = -1;
        double dmin = 10000;
        double d;
        for (int i = 0; i < points.size(); i++) {
            d = getDistance(p, points.get(i));
            if (d < dmin && d < distance) {
                res = i;
                dmin = d;
            }
        }

        return res;
    }

    public void moveActive(Point newP) {
        get(getActive()).x = newP.x;
        get(getActive()).y = newP.y;
    }

    public double getDistance (Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) +
                         Math.pow(p1.y - p2.y, 2));
    }

    public void activate (int n) {
        active = n;
    }

    public void  deactivate() {
        active = -1;
    }

    public int size() {
        return points.size();
    }

    public Point get(int index) {
        return points.get(index);
    }

    public int getActive() {
        return active;
    }
}
