import java.util.ArrayList;

/**
 * Class represents a spline as a number of arcs.
 * It could be a simple spline or parametrized spline.
 * Parameters:
 *     segments - count of segments for rendering
 *     isClosed - true for closed curve (it should be only parametrised one)
 *     isParametrized - it's obvious
 *     divideBySeqNo - used for parametrized curves. If true, values of parameter t are sequence numbers of points.
 *                     If false, values of parameter t are distances between points, t(0) = 0.
 *
 *  Methods:
 *      buildSpline - runs spline calculations (creating arcs, calculating polynom coefficients)
 *      render*     - render spline or evolute
 *
 */

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
            result.addPointArray(arc.get(i).renderSimpleArc(segments));
        }
        return result;
    }

    private PointArray renderParametrizedSpline() {
        PointArray result = new PointArray();

        for (int i = 0; i < arcX.size(); i++) {
            result.addPointArray(arcX.get(i).renderParametrizedArc(arcY.get(i), segments));
        }
        return result;
    }

    public ArrayList<Double> renderRadius() {
        ArrayList<Double> result = new ArrayList<Double>();

        for (int i = 0; i < arcX.size(); i++) {
            result.addAll(arcX.get(i).renderParametrizedArcRadius(arcY.get(i), segments));
        }
        return result;
    }

    public PointArray renderParametrizedEvolute() {
        PointArray result = new PointArray();

        for (int i = 0; i < arcX.size(); i++) {
            result.addPointArray(arcX.get(i).renderParametrizedEvoluteArc(arcY.get(i), segments));
        }

        return result;
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
                if (i < xy.size() - 1) nextT += xy.get(i).getDistanceTo(xy.get(i + 1));
            }
        }
    }
}
