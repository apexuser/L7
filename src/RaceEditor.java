import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Created by dima on 28/05/16.
 */
public class RaceEditor extends JFrame implements MouseListener, ActionListener {
    private static final long serialVersionUID = 1L;
    private PointArray points = new PointArray();
    private AkimaSpline as = new AkimaSpline(20, false, true, false);
    private JButton closeSpline;

    public RaceEditor() {
        setTitle("Редактор гонки");
        addMouseListener(this);

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                MouseEvent evt = (MouseEvent) event;
                Point p = evt.getPoint();
                if (evt.getSource() != RaceEditor.this) {
                    p = SwingUtilities.convertPoint(evt.getComponent(), p, RaceEditor.this);
                }
                if (RaceEditor.this.getBounds().contains(p)) {
                    mouseMove(p);
                }
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        //this.add(new JLabel(new ImageIcon("f1.png")));
        //System.out.println(System.getProperty("user.dir"));
        closeSpline = new JButton("Замкнуть");
        closeSpline.setBounds(10, 10, 100, 30);
        closeSpline.addActionListener(this);
        setLayout(null);
        add(closeSpline);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawPoints(points, g);
        if (points.size() > 2) {
            drawAkimaSpline(points, g, false);
            drawCircles(points, g);
        } else if (points.size() == 2) {
            g.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            int nearest = points.getInDistance(new Point(e.getX(), e.getY()), 10);
            if (nearest >= 0) {
                points.activate(nearest);
                repaint();
            } else {
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        }
    }

    private void drawAkimaSpline (PointArray points, Graphics g, boolean b) {
        PointArray spline = as.getSpline(points);

        // curve, parametrised by sequence:
        drawCurve(spline, g, Color.blue);
    }

    private void drawCurve (PointArray p, Graphics g, Color c) {
        g.setColor(c);
        for (int i = 0; i < p.size() - 1; i++) {
            g.drawLine(p.get(i).x, p.get(i).y, p.get(i + 1).x, p.get(i + 1).y);
        }
    }

    private void drawPoints (PointArray p, Graphics g) {
        g.setColor(Color.black);
        for (int i = 0; i < p.size(); i++) g.drawOval(p.get(i).x - 3, p.get(i).y - 3, 5, 5);

        if (p.getActive() >= 0) {
            g.setColor(Color.red);
            g.drawOval(p.get(p.getActive()).x - 3, p.get(p.getActive()).y - 3, 5, 5);
        }

    }

    private void drawCircles (PointArray points, Graphics g) {
        for (int i = 0; i < points.size() - 2; i++) {
            double ax = 0;// points.get(i).x;
            double ay = 0;// points.get(i).y;
            double bx = points.get(i + 1).x - points.get(i).x;
            double by = points.get(i + 1).y - points.get(i).y;
            double cx = points.get(i + 2).x - points.get(i).x;
            double cy = points.get(i + 2).y - points.get(i).y;
            double d = 2 * (ax * (by - cy) +
                            bx * (cy - ay) +
                            cx * (ay - by));

            int ox = new Double(((ax * ax + ay * ay) * (by - cy) +
                                 (bx * bx + by * by) * (cy - ay) +
                                 (cx * cx + cy * cy) * (ay - by)) / d).intValue() + points.get(i).x;
            int oy = new Double(((ax * ax + ay * ay) * (cx - bx) +
                                 (bx * bx + by * by) * (ax - cx) +
                                 (cx * cx + cy * cy) * (bx - ax)) / d).intValue() + points.get(i).y;

            int r = new Double(points.getDistance(new Point(ox, oy), points.get(i))).intValue();
            g.setColor(Color.magenta);
            g.drawOval(ox - 2, oy - 2, 5, 5);
            g.setColor(Color.red);
            g.drawOval(ox - r, oy - r, r * 2, r * 2);
        }
    }

    public void mouseMove(Point p) {
        if (points.getActive() >= 0) {
            points.moveActive(p);
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        points.deactivate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (as.isClosed()) {
            as.tear();
            closeSpline.setText("Замкнуть");
        } else {
            as.close();
            closeSpline.setText("Разомкнуть");
        }
        repaint();
    }
}