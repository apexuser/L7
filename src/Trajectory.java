import java.util.ArrayList;

/**
 * Created by dima on 24/06/16.
 */
public class Trajectory {
    public ArrayList<TrajectoryPoint> trajectoryPoints;

    public Trajectory () {
        trajectoryPoints = new ArrayList<>();
    }

    public int size () {
        return trajectoryPoints.size();
    }

    public void addPoint (Point p, double radius) {
        int n = trajectoryPoints.size();
        Point np;
        Point pp;
        switch (n) {
            case  0: trajectoryPoints.add(new TrajectoryPoint(p, p, p, radius));
                     break;
            case  1: np = trajectoryPoints.get(0).getP();
                     trajectoryPoints.add(new TrajectoryPoint(p, np, np, radius));
                     trajectoryPoints.get(0).setNeighbors(np, p, p);
                     break;
            default: np = trajectoryPoints.get(0).getP();
                     pp = trajectoryPoints.get(n - 1).getP();
                     trajectoryPoints.add(new TrajectoryPoint(p, pp, np, radius));
                     trajectoryPoints.get(0).setPrev(p);
                     trajectoryPoints.get(n - 1).setNext(p);
        }
    }
}
