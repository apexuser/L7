import java.awt.*;
import java.util.ArrayList;

/**
 * Created by dima on 29/05/16.
 */
public class AkimaSpline {
    public static int detalization = 10;

    public static PointArray plotSpline(PointArray source) {
        PointArray result = new PointArray();


        ArrayList<Double> t = getTArray(source);

        for (int i = 2; i < source.size() - 3; i++) {
            //System.out.println("Point #" + i);
            result.addPointArray(getAkimaArc(source.get(i), source.get(i + 1), t.get(i - 2), t.get(i - 1)));
            //System.out.println("---------------------------------------------");
        }
        return result;
    }

    private static PointArray getAkimaArc(Point p1, Point p2, double t1, double t2) {
        PointArray res = new PointArray();

        //System.out.println(
//                " Point 1: (" + p1.x + ", " + p1.y + ")" +
//                        " Point 2: (" + p2.x + ", " + p2.y + ")" +
//                        " t1: " + t1 + " t2: " + t2);

        double dx = (double)(p2.x - p1.x);
        double dydx = (double)(p2.y - p1.y) / dx;
        double k0 = (double)p1.y;
        double k1 = t1;
        double k2 = (3 * dydx - 2 * t1 - t2) / dx;
        double k3 = (t1 + t2 - 2 * dydx) / Math.pow(dx, 2);

        //System.out.println("k0 = " + k0 + " k1 = " + k1 + " k2 = " + k2 + " K3 = " + k3);

        double step = dx / detalization;

        res.add(p1);
        //System.out.println("Points");

        for (double xx = p1.x + step; xx < p2.x; xx += step) {
            double xpar = xx - p1.x;
            int y = new Double(k0 + xpar * (k1 + xpar * (k2 + xpar * k3))).intValue();
            int x = new Double(xx).intValue();
            res.add(new Point(x, y));
            //System.out.println("x = " + xx + " xpar = " + xpar + " y = " + y);
        }
        return res;
    }

    private static ArrayList<Double> getTArray(PointArray source) {
        ArrayList<Double> t = new ArrayList<Double>();
        //System.out.println("getTArray");
        for (int i = 2; i < source.size() - 2; i++) {
            //System.out.println("Point #" + i);

            Point p1 = source.get(i - 2);
            Point p2 = source.get(i - 1);
            Point p3 = source.get(i);
            Point p4 = source.get(i + 1);
            Point p5 = source.get(i + 2);

            //System.out.println(
//                    " Point 1: (" + p1.x + ", " + p1.y + ")" +
//                    " Point 2: (" + p2.x + ", " + p2.y + ")" +
//                    " Point 3: (" + p3.x + ", " + p3.y + ")" +
//                    " Point 4: (" + p4.x + ", " + p4.y + ")" +
//                    " Point 5: (" + p5.x + ", " + p5.y + ")");

            double m1 = (double)(p2.y - p1.y) / (double)(p2.x - p1.x);
            double m2 = (double)(p3.y - p2.y) / (double)(p3.x - p2.x);
            double m3 = (double)(p4.y - p3.y) / (double)(p4.x - p3.x);
            double m4 = (double)(p5.y - p4.y) / (double)(p5.x - p4.x);

            //System.out.println("m1 = " + m1 + " m2 = " + m2 + " m3 = " + m3 + " m4 = " + m4);

            double tt;
            if ((m1 == m2) && (m3 == m4)) {
                tt = (m2 + m3) / 2;
                t.add((m2 + m3) / 2);
            } else {
                tt = (Math.abs(m4 - m3) * m2 + Math.abs(m2 - m1) * m3) / (Math.abs(m4 - m3) + Math.abs(m2 - m1));
                t.add((Math.abs(m4 - m3) * m2 + Math.abs(m2 - m1) * m3) / (Math.abs(m4 - m3) + Math.abs(m2 - m1)));
            }
            //System.out.println("t = " + tt);
            //System.out.println("---------------------------------------------");
        }
        //System.out.println("End of getTArray");
        //System.out.println("_____________________________________________________________");

        return t;
    }
}
