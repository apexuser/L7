import java.io.*;
import java.util.ArrayList;

/**
 * Created by dima on 28/05/16.
 */
@SuppressWarnings("ALL")
public class PointArray {
    private ArrayList<Point> points;
    private int active;
    private String fileName = "test.track";
    private String delimiter = ",";

    public PointArray () {
        init();
    }

    private void init() {
        points = new ArrayList<Point>();
        active = -1;
    }

    public PointArray(PointArray source) {
        init();
        copyFrom(source);
    }

    public void add(Point p) {
        points.add(p);
    }

    public void add(int idx, Point p) {
        points.add(idx, p);
    }

    public void addUnique(Point p) {
        int i = points.indexOf(p);
        if (i < 0) {
            add(p);
        }
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
            d = p.getDistanceTo(points.get(i));
            if (d < dmin && d < distance) {
                res = i;
                dmin = d;
            }
        }

        return res;
    }

    public void save() {
        PrintWriter out;
        try {
            out = new PrintWriter(fileName);
            for (Point p : points) {
                out.println(new Double(p.x).intValue() + delimiter + new Double(p.y).intValue());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        BufferedReader br = null;
        points = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();

            while (line != null) {
                int x = Integer.parseInt(line.substring(0, line.indexOf(delimiter)));
                int y = Integer.parseInt(line.substring(line.indexOf(delimiter) + 1));
                points.add(new Point(x, y));

                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveActive(java.awt.Point newP) {
        get(getActive()).x = new Double(newP.x).intValue();
        get(getActive()).y = new Double(newP.y).intValue();
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
