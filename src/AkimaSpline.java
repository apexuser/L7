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
    private ArrayList<AkimaArc> arc;
    private ArrayList<AkimaArc> arcX;
    private ArrayList<AkimaArc> arcY;

    public AkimaSpline (int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        this.segments = segments;
        this.isClosed = isClosed;
        this.isParametrized = isParametrized;
        this.divideBySeqNo = divideBySeqNo;
        arc = new ArrayList<AkimaArc>();
        arcX = new ArrayList<AkimaArc>();
        arcY = new ArrayList<AkimaArc>();
    }
    
    public void buildSpline(PointArray source) {
        if (isParametrized) buildParametrizedSpline(source);
                       else arc = getSimpleSpline(source);

    }

    public Point getEvolutePoint (double t, AkimaArc ax, AkimaArc ay) {
        double x = getPoly(t, ax);
        double y = getPoly(t, ay);
        double dx = getDeriv1(t, ax);
        double dy = getDeriv1(t, ay);
        double ddx = getDeriv2(t, ax);
        double ddy = getDeriv2(t, ay);
        double fraction = (dx * dx + dy * dy) / (dx * ddy - ddx * dy);
        return new Point(x - dy * fraction, y + dx * fraction);
    }

    private double getPoly(double x, AkimaArc a) {
        return a.k0 + x * (a.k1 + x * (a.k2 + x * a.k3));
    }

    private double getDeriv1 (double x, AkimaArc a) {
        return a.k1 + x * (2 * a.k2 + 3 * x * a.k3);
    }

    private double getDeriv2 (double x, AkimaArc a) {
        return 2 * a.k2 + 6 * x * a.k3;
    }

    private void buildParametrizedSpline(PointArray source) {
        PointArray tx = new PointArray();
        PointArray ty = new PointArray();
        dividePoints(source, tx, ty, divideBySeqNo);

        arcX = getSimpleSpline(tx);
        arcY = getSimpleSpline(ty);
    }

    private ArrayList<AkimaArc> getSimpleSpline(PointArray source) {
        ArrayList<AkimaArc> result = new ArrayList<AkimaArc>();
        PointArray extendedSource = new PointArray();
        extendedSource.copyFrom(source);
        addExtraPoints(extendedSource);
        ArrayList<Double> t = getTArray(extendedSource);

        for (int i = 2; i < extendedSource.size() - 3; i++) {
            result.add(getAkimaArc(extendedSource.get(i), extendedSource.get(i + 1), t.get(i - 2), t.get(i - 1)));
        }
        return result;
    }

    private AkimaArc getAkimaArc(Point p1, Point p2, double t1, double t2) {
        double dx = (double)(p2.x - p1.x);
        double dydx = (double)(p2.y - p1.y) / dx;
        double k0 = (double)p1.y;
        double k1 = t1;
        double k2 = (3 * dydx - 2 * t1 - t2) / dx;
        double k3 = (t1 + t2 - 2 * dydx) / Math.pow(dx, 2);

        return new AkimaArc(k0, k1, k2, k3, p1.x, p1.y, p2.x, p2.y);
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
        double k1 = (p2.y - p1.y) / (p2.x - p1.x);
        double k2 = (p3.y - p2.y) / (p3.x - p2.x);
        p4.y = (2 * k2 - k1) * (p4.x - p3.x) + p3.y;
        double k3 = (p4.y - p3.y)/ (p4.x - p3.x);
        p5.y = (2 * k3 - k2) * (p5.x - p4.x) + p4.y;
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

    public PointArray renderSpline() {
        PointArray result = new PointArray();

        for (int i = 0; i < arcX.size(); i++) {
            result.addPointArray(renderArc(arcX.get(i), arcY.get(i)));
        }
        return result;
    }

    public PointArray renderArc(AkimaArc ax, AkimaArc ay) {
        PointArray result = new PointArray();
        double tstep = (ax.x2 - ax.x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(new Double(ax.k0 + t * (ax.k1 + t * (ax.k2 + t * ax.k3))).intValue(),
                                 new Double(ay.k0 + t * (ay.k1 + t * (ay.k2 + t * ay.k3))).intValue()));
            t += tstep;
        }
        return result;
    }

    public PointArray renderEvolute() {
        PointArray result = new PointArray();

        for (int i = 0; i < arcX.size(); i++) {
            double t = 0;
            double step = (arcX.get(i).x2 - arcX.get(i).x1) / (segments + 1);
            for (int j = 0; j <= segments; j++) {
                result.add(getEvolutePoint(t, arcX.get(i), arcY.get(i)));
                t += step;
            }
        }

        return result;
    }
}
