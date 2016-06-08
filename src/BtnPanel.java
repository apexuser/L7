import javax.swing.*;

/**
 * Created by dima on 06/06/16.
 */
public class BtnPanel extends JPanel {
    public JButton closeSpline;
    public JButton resetSpline;
    public JButton saveTrack;
    public JButton loadTrack;
    public JButton runRace;

    public BtnPanel() {
        closeSpline = initButton("Замкнуть",  10,  10, 100, 30, "close");
        resetSpline = initButton("Сброс",     10,  50, 100, 30, "reset");
        saveTrack   = initButton("Сохранить", 10,  90, 100, 30, "save");
        loadTrack   = initButton("Загрузить", 10, 130, 100, 30, "load");
        runRace     = initButton("Запуск",    10, 170, 100, 30, "run");
    }

    private JButton initButton (String title, int x, int y, int width, int height, String command) {
        JButton b = new JButton(title);
        b.setBounds(x, y, width, height);
        b.setActionCommand(command);
        this.add(b);
        return b;
    }
}
