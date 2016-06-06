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
    private AkimaSpline as = new AkimaSpline(10, false, true, false);
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
        //System.out.println(System.getProperty("user.dir"));
        closeSpline = new JButton("Замкнуть");
        closeSpline.setBounds(10, 10, 100, 30);
        closeSpline.addActionListener(this);
        setLayout(null);
        add(closeSpline);
//        this.add(new JLabel(new ImageIcon("f1.png")));
        points.add(new Point(329, 435));
        points.add(new Point(341, 362));
        points.add(new Point(622, 495));

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawPoints(points, g);
        if (points.size() > 2) {
            drawAkimaSpline(points, g, false);
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
        drawCircles(spline, g);
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

    private Point circleCenter (Point pa, Point pb, Point pc) {
        double ax = 0;// points.get(i).x;
        double ay = 0;// points.get(i).y;
        double bx = pb.x - pa.x;
        double by = pb.y - pa.y;
        double cx = pc.x - pa.x;
        double cy = pc.y - pa.y;
        double d = 2 * (ax * (by - cy) +
                bx * (cy - ay) +
                cx * (ay - by));

        int ox = new Double(((ax * ax + ay * ay) * (by - cy) +
                (bx * bx + by * by) * (cy - ay) +
                (cx * cx + cy * cy) * (ay - by)) / d).intValue() + pa.x;
        int oy = new Double(((ax * ax + ay * ay) * (cx - bx) +
                (bx * bx + by * by) * (ax - cx) +
                (cx * cx + cy * cy) * (bx - ax)) / d).intValue() + pa.y;
        return new Point(ox, oy);
    }

    private void drawCircles (PointArray points, Graphics g) {
        Point prev = null;
        for (int i = 0; i < points.size() - 2; i++) {
            g.setColor(Color.magenta);
            Point p = circleCenter(points.get(i), points.get(i + 1), points.get(i + 2));
            g.drawOval(p.x - 2, p.y - 2, 5, 5);

            if (prev != null) {
                g.drawLine(p.x, p.y, prev.x, prev.y);
            }
            prev = p;
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