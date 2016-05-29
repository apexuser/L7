/**
 * Created by dima on 27/05/16.
 */

public class Race {
    public static void main (String[] args) {
        Car car = new Car();

        RaceEditor re = new RaceEditor();
        re.setSize(1400, 700);
        re.setVisible(true);
        re.setDefaultCloseOperation(RaceEditor.EXIT_ON_CLOSE);

        /*
        for (int i = 0; i < 10000; i++) {
            car.run(1, 0, 0, 0.01);
            if (i % 100 == 0) {
                System.out.println("Time: " + i + " speed: " + car.velocityX + " position: " + car.x + " mass: " + car.getMass());
            }
        }*/
    }
}
