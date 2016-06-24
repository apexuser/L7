/**
 * Created by dima on 24/06/16.
 */
public class TrajectoryPoint {
    private Point p;
    private Point prev;
    private Point next;
    public double radius;
    public double angle;

    public TrajectoryPoint (Point p, Point prev, Point next, double radius) {
        this.radius = radius;
        setNeighbors(p, prev, next);
    }

    private void setAngle() {
        double vx0 = p.x - prev.x;
        double vy0 = p.y - prev.y;
        double vx1 = next.x - p.x;
        double vy1 = next.y - p.y;
        double modv0 = Math.sqrt(vx0 * vx0 + vy0 * vy0);
        double modv1 = Math.sqrt(vx1 * vx1 + vy1 * vy1);

        angle = Math.acos((vx0 * vx1 + vy0 * vy1) / (modv0 * modv1));
    }

    public void setNeighbors (Point p, Point prev, Point next) {
        this.p = p;
        this.prev = prev;
        this.next = next;
        setAngle();
    }

    public void setP(Point p) {
        this.p = p;
        setAngle();
    }

    public void setPrev(Point prev) {
        this.prev = prev;
        setAngle();
    }

    public void setNext(Point next) {
        this.next = next;
        setAngle();
    }

    public Point getP() {
        return p;
    }

    public Point getPrev() {
        return prev;
    }

    public Point getNext() {
        return next;
    }
}
