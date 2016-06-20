import java.util.ArrayList;
import java.util.Map;

public abstract class Spline {
    protected int segments;
    protected boolean isClosed;
    protected boolean isParametrized;
    protected boolean divideBySeqNo;
    protected ArrayList<Arc> arc;
    protected ArrayList<Arc> arcX;
    protected ArrayList<Arc> arcY;
    protected PointArray tx;
    protected PointArray ty;
    protected PointArray xy;

    public Spline (int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        this.segments = segments;
        this.isClosed = isClosed;
        this.isParametrized = isParametrized;
        this.divideBySeqNo = divideBySeqNo;
        arc = new ArrayList<Arc>();
        arcX = new ArrayList<Arc>();
        arcY = new ArrayList<Arc>();
    }

    protected void buildParametrizedSpline(PointArray source) {
        xy = new PointArray(source);
        if (isClosed) extendClosed(xy);
        dividePoints(xy);
        arcX = getSimpleSpline(tx);
        arcY = getSimpleSpline(ty);
    }

    protected void extendClosed(PointArray source) {

    }

    protected abstract ArrayList<Arc> getSimpleSpline(PointArray source);

    public void buildSpline(PointArray source) {
        if (isParametrized) buildParametrizedSpline(source);
        else arc = getSimpleSpline(source);

    }

    private double getPoly(double x, Arc a) {
        return a.k0 + x * (a.k1 + x * (a.k2 + x * a.k3));
    }

    private double getDeriv1 (double x, Arc a) {
        return a.k1 + x * (2 * a.k2 + 3 * x * a.k3);
    }

    private double getDeriv2 (double x, Arc a) {
        return 2 * a.k2 + 6 * x * a.k3;
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
        if (isParametrized) return renderParametrizedSpline();
        else return renderSimpleSpline();
    }

    private PointArray renderSimpleSpline() {
        PointArray result = new PointArray();

        for (int i = 0; i < arc.size(); i++) {
            result.addPointArray(renderSimpleArc(arc.get(i)));
        }
        return result;
    }

    private PointArray renderSimpleArc(Arc xy) {
        PointArray result = new PointArray();
        double tstep = (xy.x2 - xy.x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(t + xy.x1, xy.k0 + t * (xy.k1 + t * (xy.k2 + t * xy.k3))));
            t += tstep;
        }
        return result;
    }

    private PointArray renderParametrizedSpline() {
        PointArray result = new PointArray();

        for (int i = 0; i < arcX.size(); i++) {
            result.addPointArray(renderParametrizedArc(arcX.get(i), arcY.get(i)));
        }
        return result;
    }

    private PointArray renderParametrizedArc(Arc ax, Arc ay) {
        PointArray result = new PointArray();
        double tstep = (ax.x2 - ax.x1) / segments;
        double t = 0;

//        System.out.print("arc x: ");
//        ax.debugPrint();
//        System.out.print("arc y: ");
//        ay.debugPrint();

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(ax.k0 + t * (ax.k1 + t * (ax.k2 + t * ax.k3)),
                                (ay.k0 + t * (ay.k1 + t * (ay.k2 + t * ay.k3)))));
            t += tstep;
        }
        return result;
    }

    public ArrayList<Double> renderRadius() {
        ArrayList<Double> result = new ArrayList<Double>();

        for (int i = 0; i < arcX.size(); i++) {
            result.addAll(renderArcRadius(arcX.get(i), arcY.get(i)));
        }
        return result;
    }

    private ArrayList<Double> renderArcRadius(Arc ax, Arc ay) {
        ArrayList<Double> result = new ArrayList<Double>();
        double t = 0;//ax.x1;
        double step = (ax.x2 - ax.x1) / (segments + 1);

//        System.out.print("arc x: ");
//        ax.debugPrint();
//        System.out.print("arc y: ");
//        ay.debugPrint();

        for (int j = 0; j <= segments; j++) {
            double dx = getDeriv1(t, ax);
            double dy = getDeriv1(t, ay);
            double ddx = getDeriv2(t, ax);
            double ddy = getDeriv2(t, ay);

            result.add(new Double(Math.pow((dx * dx + dy * dy), 3/2) / (dx * ddy - dy * ddx)));
//            System.out.println("t = " + t + " dx = " + dx + " dy = " + dy + " ddx = " + ddx + " ddy = " + ddy +
//                 " r = " + Math.pow((dx * dx + dy * dy), 3/2) / (dx * ddy - dy * ddx));
            t += step;
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

    public Point getEvolutePoint (double t, Arc ax, Arc ay) {
        double x = getPoly(t, ax);
        double y = getPoly(t, ay);
        double dx = getDeriv1(t, ax);
        double dy = getDeriv1(t, ay);
        double ddx = getDeriv2(t, ax);
        double ddy = getDeriv2(t, ay);
        double fraction = (dx * dx + dy * dy) / (dx * ddy - ddx * dy);
        return new Point(x - dy * fraction, y + dx * fraction);
    }

    protected void dividePoints(PointArray xy) {
        double nextT = 0;
        tx = new PointArray();
        ty = new PointArray();

        for (int i = 0; i < xy.size(); i++) {
            tx.add(new Point(nextT, xy.get(i).x));
            ty.add(new Point(nextT, xy.get(i).y));

            if (divideBySeqNo) {
                nextT = i + 1;
            } else {
                if (i < xy.size() - 1) nextT += xy.getDistance(xy.get(i), xy.get(i + 1));
            }
        }
    }
}
