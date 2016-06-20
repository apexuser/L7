import java.util.ArrayList;

/**
 * Created by dima on 05/06/16.
 */
public class Arc {
    public double k0;
    public double k1;
    public double k2;
    public double k3;
    public double x1;
    public double y1;
    public double x2;
    public double y2;

    public Arc(double k0, double k1, double k2, double k3, double x1, double y1, double x2, double y2) {
        this.k0 = k0;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getPolynomValue(double x) {
        return k0 + x * (k1 + x * (k2 + x * k3));
    }

    public double get1Derivative(double x) {
        return k1 + x * (2 * k2 + 3 * x * k3);
    }

    public double get2Derivative(double x) {
        return 2 * k2 + 6 * x * k3;
    }

    public void debugPrint() {
        System.out.println(" k0 = " + k0 + " k1 = " + k1 + " k2 = " + k2 + " k3 = " + k3 + " x1 = " + x1 + " y1 = " + y1);
    }

    public PointArray renderSimpleArc(int segments) {
        PointArray result = new PointArray();
        double tstep = (x2 - x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(t + x1, getPolynomValue(t)));
            t += tstep;
        }
        return result;
    }

    public PointArray renderParametrizedArc(Arc ay, int segments) {
        PointArray result = new PointArray();
        double tstep = (x2 - x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(getPolynomValue(t), ay.getPolynomValue(t)));
            t += tstep;
        }
        return result;
    }

    public ArrayList<Double> renderParametrizedArcRadius(Arc ay, int segments) {
        ArrayList<Double> result = new ArrayList<Double>();
        double t = 0;
        double step = (x2 - x1) / segments;

        for (int j = 0; j <= segments; j++) {
            double dx = get1Derivative(t);
            double dy = ay.get1Derivative(t);
            double ddx = get2Derivative(t);
            double ddy = ay.get2Derivative(t);

            result.add(new Double(Math.pow((dx * dx + dy * dy), 3/2) / (dx * ddy - dy * ddx)));
            t += step;
        }

        return result;
    }

    public Point getParametrizedEvolutePoint(double t, Arc ay) {
        double x = getPolynomValue(t);
        double y = ay.getPolynomValue(t);
        double dx = get1Derivative(t);
        double dy = ay.get1Derivative(t);
        double ddx = get2Derivative(t);
        double ddy = ay.get2Derivative(t);
        double fraction = (dx * dx + dy * dy) / (dx * ddy - ddx * dy);
        return new Point(x - dy * fraction, y + dx * fraction);
    }

    public PointArray renderParametrizedEvoluteArc(Arc ay, int segments) {
        PointArray result = new PointArray();
        double t = 0;
        double step = (x2 - x1) / segments;
        for (int j = 0; j <= segments; j++) {
            result.add(getParametrizedEvolutePoint(t, ay));
            t += step;
        }
        return result;
    }
}
