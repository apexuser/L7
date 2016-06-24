import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dima on 07/06/16.
 */
public class Driver {
    private int experience = 0;
    private String name = "Vasya";
    private double thoroughness = 0.8;
    private double precision = 0.8;
    private Car car;

    private Trajectory trajectory;

    public Driver () {
        car = new Car();
        trajectory = new Trajectory();
    }

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

    public PointArray prepareRoute(PointArray points) {
        PointArray result = new PointArray();
        AkimaSpline as = new AkimaSpline(10, false, true, false);
        Trajectory t = as.buildTrajectory(points);
//        ArrayList<Double> radius = new ArrayList<Double>();
//        int distance = 5;
//
//        for (int i = 0; i < route.size() - 1; i++) {
//            radius.add(route.get(i).getDistanceTo(evolute.get(i)));
//        }
//
//        double sgnCurrent;
//        double sgnPrevious = Math.signum(radius.get(1) - radius.get(0));
//
//        for (int i = 2; i < radius.size(); i++) {
//            sgnCurrent = Math.signum(radius.get(i) - radius.get(i - 1));
//
//            System.out.println(radius.get(i));
//
//            if (sgnCurrent != sgnPrevious) {
//                if (isLocalMax(radius, i, distance)) {
//                    result.addUnique(route.get(i));
////                    System.out.println(" Added value: " + radius.get(i));
//                } else {
////                    System.out.println("");
//                }
//            } else {
////                System.out.println("");
//            }
//
//            sgnPrevious = sgnCurrent;
//        }
//
        return result;
    }

    private boolean isLocalMax(ArrayList<Double> source, int position, int distance) {
        double max = 0;
        int maxIndex = -1;
        int from = Math.max(position - distance, 0);
        int to   = Math.min(position + distance, source.size());

        for (int i = from; i < to; i++) {
            if (max < source.get(i)) {
                max = source.get(i);
                maxIndex = i;
            }
         //   System.out.println("    i = " + i + " source.get(i) = " + source.get(i) + " max = " + max);
        }
        return maxIndex == position;
    }

    // vmax = sqrt(vnext ^ 2 - 2 * a * l)
/*    public PointArray prepareRoute(PointArray route, PointArray evolute) {
        PointArray result = new PointArray();
        ArrayList<Double> radius = new ArrayList<Double>();
        int length = 10;

        for (int i = 0; i < route.size() - 1; i++) {
            radius.add(1/route.getDistance(route.get(i), evolute.get(i)));
        }

        for (int i = 0; i < route.size() - 10; i++) {
            int maxIndex = getLocalMax(radius, i, length);
            if (maxIndex != -1) {
                result.addUnique(route.get(maxIndex));
                length++;
            } else length = 10;

            System.out.println("Value: " + radius.get(i) + " lmax = " + maxIndex);
        }

        return result;
    }

    private int getLocalMax(ArrayList<Double> source, int position, int length) {
        double max = 0;
        int maxIndex = -1;
        for (int i = position; i <= (position + length) && i < source.size(); i++) {
            if (max < source.get(i)) {
                max = source.get(i);
                maxIndex = i;
            }
            System.out.println("    i = " + i + " source.get(i) = " + source.get(i) + " max = " + max);
        }
        if (maxIndex == position || maxIndex == (position + length)) {
            return  0;
        } else {
            return maxIndex;
        }
    }*/
}

