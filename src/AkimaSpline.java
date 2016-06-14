import java.util.ArrayList;

/**
 * Created by dima on 29/05/16.
 */
public class AkimaSpline extends Spline {

    public AkimaSpline(int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        super(segments, isClosed, isParametrized, divideBySeqNo);
    }

    @Override
    protected ArrayList<Arc> getSimpleSpline(PointArray source) {
        ArrayList<Arc> result = new ArrayList<Arc>();
        PointArray extendedSource = new PointArray();
        extendedSource.copyFrom(source);
        addExtraPoints(extendedSource);
        ArrayList<Double> t = getTArray(extendedSource);

        for (int i = 2; i < extendedSource.size() - 3; i++) {
            result.add(getAkimaArc(extendedSource.get(i), extendedSource.get(i + 1), t.get(i - 2), t.get(i - 1)));
        }
        return result;
    }

    private Arc getAkimaArc(Point p1, Point p2, double t1, double t2) {
        double dx = (p2.x - p1.x);
        double dydx = (p2.y - p1.y) / dx;
        double k0 = p1.y;
        double k1 = t1;
        double k2 = (3 * dydx - 2 * t1 - t2) / dx;
        double k3 = (t1 + t2 - 2 * dydx) / Math.pow(dx, 2);

        return new Arc(k0, k1, k2, k3, p1.x, p1.y, p2.x, p2.y);
    }

    private static ArrayList<Double> getTArray(PointArray source) {
        ArrayList<Double> t = new ArrayList<Double>();
        for (int i = 2; i < source.size() - 2; i++) {
            Point p1 = source.get(i - 2);
            Point p2 = source.get(i - 1);
            Point p3 = source.get(i);
            Point p4 = source.get(i + 1);
            Point p5 = source.get(i + 2);
            double m1 = (p2.y - p1.y) / (p2.x - p1.x);
            double m2 = (p3.y - p2.y) / (p3.x - p2.x);
            double m3 = (p4.y - p3.y) / (p4.x - p3.x);
            double m4 = (p5.y - p4.y) / (p5.x - p4.x);

            if ((m1 == m2) && (m3 == m4)) {
                t.add((m2 + m3) / 2);
            } else {
                t.add((Math.abs(m4 - m3) * m2 + Math.abs(m2 - m1) * m3) / (Math.abs(m4 - m3) + Math.abs(m2 - m1)));
            }
        }
        
        return t;
    }

    @Override
    protected void addExtraPoints(PointArray source) {
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
         } else {
            // points before:
            Point p0 = source.get(0);
            Point p1 = source.get(1);
            Point p2 = source.get(2);
            // points after:
            Point pn = source.get(source.size() - 1);
            Point pn1 = source.get(source.size() - 2);

            source.add(0, pn1);
            source.add(1, pn);
            source.add(p0);
            source.add(p1);
            source.add(p2);
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

}
