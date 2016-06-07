import java.util.Random;

/**
 * Created by dima on 07/06/16.
 */
public class Driver {
    private int experience = 0;
    private String name = "Vasya";
    private double thoroughness = 0.8;
    private double precision = 0.8;

    public Command getCommand(Command expectedCommand) {
        double newThrottle = getRandomizedValue(expectedCommand.throttle, getModifier(expectedCommand.throttle));
        double newBraking  = getRandomizedValue(expectedCommand.braking,  getModifier(expectedCommand.braking));
        double newSteering = getRandomizedValue(expectedCommand.steering, 0);
        Command result = new Command(newThrottle, newBraking, newSteering, expectedCommand.gearShiftTo);
        return result;
    }

    private double getRandomizedValue(double value, int modifier) {
        Random r = new Random();
        double errorProbability = (modifier == 0) ? (1 - thoroughness) : (1 - thoroughness) * modifier;
        double deltaValue = (r.nextDouble() < errorProbability) ? r.nextDouble() * precision : 0;
        double sign = r.nextBoolean() ? 1 : -1;
        return value * (1 + sign * deltaValue);
    }

    private int getModifier(double value) {
        if (value >= 0 && value <= 0.1) {
            return new Double(Math.round((1 - value) * 100) / 100).intValue();
        } else if (value >= 0.9 && value <= 1) {
            return new Double(Math.round(value * 100) / 100).intValue();
        } else {
            return 0;
        }
    }
}

