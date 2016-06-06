import javax.swing.*;

/**
 * Created by dima on 06/06/16.
 */
public class BtnPanel extends JPanel {
    public JButton closeSpline;
    public JButton resetSpline;

    public BtnPanel() {
        closeSpline = new JButton("Замкнуть");
        closeSpline.setBounds(10, 10, 100, 30);
        closeSpline.setActionCommand("close");
        resetSpline = new JButton("Сброс");
        resetSpline.setBounds(120, 10, 100, 30);
        resetSpline.setActionCommand("reset");
        this.add(closeSpline);
        this.add(resetSpline);
    }
}
