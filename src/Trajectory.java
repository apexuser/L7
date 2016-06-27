import java.util.ArrayList;

/**
 * Created by dima on 24/06/16.
 */
public class Trajectory {
    public ArrayList<TrajectoryPoint> trajectoryPoints;
//    public Car car;

    public Trajectory () {
        trajectoryPoints = new ArrayList<>();
//        this.car = car;
    }



    public int size () {
        return trajectoryPoints.size();
    }

    public void makeTrajectoryPlan (Car car) {
        double vPlan = 1000000; // just very big number
        double vnext;
        double vmax;
        for (int i = size() - 1; i >=0; i--) {
            vnext = vPlan;
            vmax = car.getMaxTurnSpeed(tp(i).radius);
            vPlan = Math.min(vmax, car.getSpeedBeforeBrake(vnext, tp(i).getDistance()));
            tp(i).vPlan = vPlan;
        }
    }

    public void addPoint (Point p, double radius) {
        int n = size();
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

    private TrajectoryPoint tp (int i) {
        return trajectoryPoints.get(i);
    }
}
