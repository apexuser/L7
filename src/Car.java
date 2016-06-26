/**
 * Created by dima on 27/05/16.
 */
public class Car {
    // initial parameters
    public double torque = 7000;        // N * m
    public double mass = 702;           // kg
    public double fuel = 100;           // kg
    public double wheelRadius = 0.66;   // m
    public double consumption = 0.01;   // kg/s

    public double airResistance = 0.8;   // -
    public double frictionResistance = 24;
    public double tireFriction = 20;

    // race parameters:
    public double velocityX = 0;
    public double velocityY = 0;
    public double x = 0;
    public double y = 0;
    public double orientation = 0;

    public double getCentrifugalForce(double turnRadius) {
        return Math.pow(getVelocity(), 2) / turnRadius;
    }

    //public double steer = 0;

    public void run (double traction, double steer, double breaks, double time) {
        double deltaVelocity = getAcceleration(traction, 5) * time;

        velocityX = velocityX + deltaVelocity;
        x = x + velocityX * time;
        burnFuel(traction, time);
    }
    public void run (Command c) {

    }

    public double getMaxTurnSpeed(double radius) { return Math.sqrt(tireFriction * radius); }

    public double getVBrake (double vnext, double distance) { return Math.sqrt(vnext * vnext - 2 * tireFriction * distance); }

    private double getFullFrictionResistance() { return airResistance * Math.pow(getVelocity(), 2) + frictionResistance * getVelocity(); }

    public double getMass() { return mass + fuel; }

    private void burnFuel(double traction, double time) { fuel -= consumption * traction * time; }

    public double getVelocity() { return Math.sqrt(velocityX * velocityX + velocityY * velocityY); }

    public double getAcceleration (double throttle, int gear) {
        double currentTorque = torque * throttle;
        double force = currentTorque / wheelRadius;
        double fractionForce = getFullFrictionResistance();

        return (force - fractionForce) / getMass();
    }

    public double getBrakingAcceleration () {
        return (getFullFrictionResistance() + tireFriction) / getMass();
    }
}
