import java.awt.*;
import java.util.ArrayList;

/**
 * Created by dima on 29/05/16.
 */
public class AkimaSpline {
    private int segments;
    private boolean isClosed;
    private boolean isParametrized;
    private boolean divideBySeqNo;
    private PointArray extendedPoints;

    public AkimaSpline (int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        this.segments = segments;
        this.isClosed = isClosed;
        this.isParametrized = isParametrized;
        this.divideBySeqNo = divideBySeqNo;
        extendedPoints = new PointArray();
    }
    
    public PointArray getSpline(PointArray source) {
        if (isParametrized) return getParametrizedSpline(source);
                       else return getSimpleSpline(source);

    }

    private PointArray getParametrizedSpline(PointArray source) {
        PointArray tx = new PointArray();
        PointArray ty = new PointArray();
        dividePoints(source, tx, ty, divideBySeqNo);

        return mergePoints(getSimpleSpline(tx), getSimpleSpline(ty));
    }

    private PointArray getSimpleSpline(PointArray source) {
        PointArray result = new PointArray();
        PointArray extendedSource = new PointArray();
        extendedSource.copyFrom(source);
        addExtraPoints(extendedSource);
        ArrayList<Double> t = getTArray(extendedSource);

        for (int i = 2; i < extendedSource.size() - 3; i++) {
            result.addPointArray(getAkimaArc(extendedSource.get(i), extendedSource.get(i + 1), t.get(i - 2), t.get(i - 1)));
        }
        return result;
    }

    private PointArray getAkimaArc(Point p1, Point p2, double t1, double t2) {
        PointArray res = new PointArray();

        double dx = (double)(p2.x - p1.x);
        double dydx = (double)(p2.y - p1.y) / dx;
        double k0 = (double)p1.y;
        double k1 = t1;
        double k2 = (3 * dydx - 2 * t1 - t2) / dx;
        double k3 = (t1 + t2 - 2 * dydx) / Math.pow(dx, 2);

        double step = dx / segments;

        res.add(p1);
        for (double xx = p1.x + step; xx < p2.x + step; xx += step) {
            if (xx > p2.x) xx = p2.x; // to remove possible line breaks
            double xpar = xx - p1.x;
            int y = new Double(k0 + xpar * (k1 + xpar * (k2 + xpar * k3))).intValue();
            int x = new Double(xx).intValue();
            res.add(new Point(x, y));
        }
        return res;
    }

    private static ArrayList<Double> getTArray(PointArray source) {
        ArrayList<Double> t = new ArrayList<Double>();
        for (int i = 2; i < source.size() - 2; i++) {
            Point p1 = source.get(i - 2);
            Point p2 = source.get(i - 1);
            Point p3 = source.get(i);
            Point p4 = source.get(i + 1);
            Point p5 = source.get(i + 2);
            double m1 = (double)(p2.y - p1.y) / (double)(p2.x - p1.x);
            double m2 = (double)(p3.y - p2.y) / (double)(p3.x - p2.x);
            double m3 = (double)(p4.y - p3.y) / (double)(p4.x - p3.x);
            double m4 = (double)(p5.y - p4.y) / (double)(p5.x - p4.x);

            if ((m1 == m2) && (m3 == m4)) {
                t.add((m2 + m3) / 2);
            } else {
                t.add((Math.abs(m4 - m3) * m2 + Math.abs(m2 - m1) * m3) / (Math.abs(m4 - m3) + Math.abs(m2 - m1)));
            }
        }
        
        return t;
    }

    private void dividePoints(PointArray source, PointArray tx, PointArray ty, boolean divideBySeqNo) {
        int nextT = 0;
        PointArray xy = new PointArray();
        xy.copyFrom(source);
        if (isClosed) {
            // points before:
            Point p0 = xy.get(0);
            Point p1 = xy.get(1);
            Point p2 = xy.get(2);
            // points after:
            Point pn = xy.get(xy.size() - 1);
            Point pn1 = xy.get(xy.size() - 2);

            xy.add(0, pn1);
            xy.add(1, pn);
            xy.add(p0);
            xy.add(p1);
            xy.add(p2);
        }

        for (int i = 0; i < xy.size(); i++) {
            tx.add(new Point(nextT, xy.get(i).x));
            ty.add(new Point(nextT, xy.get(i).y));

            if (divideBySeqNo) {
                nextT = i + 1;
            } else {
                if (i < xy.size() - 1) nextT += new Double(xy.getDistance(xy.get(i), xy.get(i + 1))).intValue();
            }
        }
    }

    private PointArray mergePoints (PointArray tx, PointArray ty) {
        PointArray merged = new PointArray();
        for (int i = 0; i < tx.size(); i++) {
            merged.add(new Point(tx.get(i).y, ty.get(i).y));
        }
        return  merged;
    }

    private void addExtraPoints(PointArray source) {
         if (!isClosed) {
             // points before:
             Point p1 = source.get(2);
             Point p2 = source.get(1);
             Point p3 = source.get(0);
             Point p4 = new Point();
             Point p5 = new Point();
             calculateExtraPoints(p1, p2, p3, p4, p5);
             source.add(0, p4);
             source.add(0, p5);

             // points after:
             int last = source.size() - 1;
             p1 = source.get(last - 2);
             p2 = source.get(last - 1);
             p3 = source.get(last);
             p4 = new Point();
             p5 = new Point();
             calculateExtraPoints(p1, p2, p3, p4, p5);
             source.add(p4);
             source.add(p5);
         }
    }

    private void calculateExtraPoints(Point p1, Point p2, Point p3, Point p4, Point p5) {
        p4.x = p3.x - p1.x + p2.x;
        p5.x = 2 * p3.x - p1.x;
        double k1 = (double)(p2.y - p1.y)/ (double)(p2.x - p1.x);
        double k2 = (double)(p3.y - p2.y)/ (double)(p3.x - p2.x);
        p4.y = new Double((2 * k2 - k1) * (double)(p4.x - p3.x) + p3.y).intValue();
        double k3 = (double)(p4.y - p3.y)/ (double)(p4.x - p3.x);
        p5.y = new Double((2 * k3 - k2) * (double)(p5.x - p4.x) + p4.y).intValue();
    }

    public void close() {
        isClosed = true;
    }

    public void tear() {
        isClosed = false;
    }

    public boolean isClosed() {
        return isClosed;
    }
}
