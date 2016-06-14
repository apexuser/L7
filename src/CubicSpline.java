import java.util.ArrayList;

/**
 * Created by dima on 14/06/16.
 */
public class CubicSpline extends Spline {

    public CubicSpline(int segments, boolean isClosed, boolean isParametrized, boolean divideBySeqNo) {
        super(segments, isClosed, isParametrized, divideBySeqNo);
    }

    @Override
    protected ArrayList<Arc> getSimpleSpline(PointArray source) {
        ArrayList<Arc> result = new ArrayList<Arc>();
        int n = source.size();

        for (int i = 0; i < n; i++) {
            Point p = source.get(i);
            Point pnx = (i == n - 1) ? new Point(0, 0) : source.get(i + 1);

            result.add(new Arc(p.y, 0, 0, 0, p.x, p.y, pnx.x, pnx.y));
        }

        result.get(0).k2 = 0.0; result.get(n - 1).k2 = 0.0;


        double[] alpha = new double[n - 1];
        double[] beta  = new double[n - 1];
        alpha[0] = 0.0;
        beta[0] = 0.0;

        for (int i = 1; i < n - 1; i++) {
            double hi  = source.get(i).x - source.get(i - 1).x;
            double hi1 = source.get(i + 1).x - source.get(i).x;
            double A = hi;
            double C = 2.0 * (hi + hi1);
            double B = hi1;
            double F = 6.0 * ((source.get(i + 1).y - source.get(i).y) / hi1 - (source.get(i).y - source.get(i - 1).y) / hi);
            double z = (A * alpha[i - 1] + C);
            alpha[i] = -B / z;
            beta[i] = (F - A * beta[i - 1]) / z;
        }

        for (int i = n - 2; i > 0; i--) {
            result.get(i).k2 = alpha[i] * result.get(i + 1).k2 + beta[i];
        }

        for (int i = n - 1; i > 0; i--)
        {
            double hi = source.get(i).x - source.get(i - 1).x;
            result.get(i).k3 = (result.get(i).k2 - result.get(i - 1).k2) / hi;
            result.get(i).k1 = hi * (2.0 * result.get(i).k2 + result.get(i - 1).k2) / 6.0 + (source.get(i).y - source.get(i - 1).y) / hi;
        }

        // multiply k2 * 2 and k3 * 6:
        for (int i = 0; i < result.size(); i++) {
            result.get(i).k2 = result.get(i).k2 / 2;
            result.get(i).k3 = result.get(i).k3 / 6;
        }

        return result;
    }
}
