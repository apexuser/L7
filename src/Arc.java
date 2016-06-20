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

    public void debugPrint() {
        System.out.println(" k0 = " + k0 + " k1 = " + k1 + " k2 = " + k2 + " k3 = " + k3 + " x1 = " + x1 + " y1 = " + y1);
    }
}
