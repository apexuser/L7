/**
 * Created by dima on 07/06/16.
 */
public class Command {
    public double throttle;
    public double braking;
    public double steering;
    public int gearShiftTo;

    public Command (double throttle, double braking, double steering, int gearShiftTo) {
        this.throttle = throttle;
        this.braking = braking;
        this.steering = steering;
        this.gearShiftTo = gearShiftTo;
    }
}