/**
 * Created by dima on 22/06/16.
 */
public class PlanPoint {
    private double r;
    private double vmax;
    private double vPlan;
    private Command command;

    public PlanPoint(Car car, double r, double vnext, double distance) {
        this.r = r;
        vmax = Math.sqrt(car.tireFriction * r);
        vPlan = Math.min(vmax, getVBrake(vnext, car.tireFriction, distance));

        double acc = getAcceleration(vPlan, vnext, distance);
        double throttle = 0;
        double braking = 0;
        double steering = 0;
        int gearsTo = 0;

        if (acc > 0) {
            double maxAcc = car.getAcceleration(1, 1);
            throttle = (acc > maxAcc) ? 1 : acc / maxAcc;
        } else {
            double maxAcc = car.getBrakingAcceleration();
            braking = (Math.abs(acc) > maxAcc) ? 1 : Math.abs(acc) / maxAcc;
        }

        command = new Command(throttle, braking, steering, gearsTo);
    }

    public double getVBrake (double vnext, double aBraking, double distance) {
        return Math.sqrt(vnext * vnext - 2 * aBraking * distance);
    }

    public double getAcceleration (double v0, double v1, double l) {
        double dv = v1 - v0;
        return (2 * v0 * dv + dv * dv) / (2 * l);
    }
}
