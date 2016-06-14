import java.util.ArrayList;

/**
 * Created by dima on 14/06/16.
 */
public abstract class Spline {
    protected int segments;
    protected boolean isClosed;
    protected boolean isParametrized;
    protected boolean divideBySeqNo;
    protected ArrayList<Arc> arc;
    protected ArrayList<Arc> arcX;
    protected ArrayList<Arc> arcY;

    public Spline (int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        this.segments = segments;
        this.isClosed = isClosed;
        this.isParametrized = isParametrized;
        this.divideBySeqNo = divideBySeqNo;
        arc = new ArrayList<Arc>();
        arcX = new ArrayList<Arc>();
        arcY = new ArrayList<Arc>();
    }

    public void buildSpline(PointArray source) {
        if (isParametrized) buildParametrizedSpline(source);
        else arc = getSimpleSpline(source);
    }

    protected void buildParametrizedSpline(PointArray source) {
        PointArray tx = new PointArray();
        PointArray ty = new PointArray();
        dividePoints(source, tx, ty, divideBySeqNo);

        arcX = getSimpleSpline(tx);
        arcY = getSimpleSpline(ty);
    }

    protected void dividePoints(PointArray source, PointArray tx, PointArray ty, boolean divideBySeqNo) {
        double nextT = 0;
        PointArray xy = new PointArray();
        xy.copyFrom(source);
        addExtraPoints(xy);

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

    protected void addExtraPoints(PointArray source) {

    }

    protected abstract ArrayList<Arc> getSimpleSpline(PointArray source);

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

    public PointArray renderSimpleArc(Arc xy) {
        PointArray result = new PointArray();
        double tstep = (xy.x2 - xy.x1) / segments;
        double t = xy.x1;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(t, xy.k0 + t * (xy.k1 + t * (xy.k2 + t * xy.k3))));
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

    public PointArray renderParametrizedArc(Arc ax, Arc ay) {
        PointArray result = new PointArray();
        double tstep = (ax.x2 - ax.x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(ax.k0 + t * (ax.k1 + t * (ax.k2 + t * ax.k3)),
                                (ay.k0 + t * (ay.k1 + t * (ay.k2 + t * ay.k3)))));
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

    protected Point getEvolutePoint (double t, Arc ax, Arc ay) {
        double x = getPoly(t, ax);
        double y = getPoly(t, ay);
        double dx = getDeriv1(t, ax);
        double dy = getDeriv1(t, ay);
        double ddx = getDeriv2(t, ax);
        double ddy = getDeriv2(t, ay);
        double fraction = (dx * dx + dy * dy) / (dx * ddy - ddx * dy);
        return new Point(x - dy * fraction, y + dx * fraction);
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
}
