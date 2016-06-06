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
    private JButton resetSpline;

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
        closeSpline.setActionCommand("close");
        resetSpline = new JButton("Сброс");
        resetSpline.setBounds(120, 10, 100, 30);
        resetSpline.addActionListener(this);
        resetSpline.setActionCommand("reset");

        this.add(new JLabel(new ImageIcon("f1.png")));
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
        //testFill(g);
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

    private void testFill(Graphics g) {
        for (int i = 0; i < 256; i++) {
            Color c = new Color(i, 0, 255 - i);
            g.setColor(c);
            g.drawLine(100, i + 100, 200, i + 100);
        }
    }

    private void drawAkimaSpline (PointArray points, Graphics g, boolean b) {
        as.buildSpline(points);
        PointArray spline = renderSpline(as);


        // curve, parametrised by sequence:
      //  drawCurve(spline, g, Color.blue);
      //  drawCircles(spline, g);
        PointArray evolute = renderEvolute(as);
        drawColored(spline, evolute, g);
        // curve, parametrised by sequence:
      //  drawCurve(evolute, g, Color.magenta);
       // System.out.println("Spline size = " + spline.size() + " evolute size = " + evolute.size());
    }

    private void drawColored(PointArray spline, PointArray evolute, Graphics g) {
        //find max and min
        double max = 0;
        double min = 1000000;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < spline.size(); i++) {
            double r = Math.log(spline.getDistance(spline.get(i), evolute.get(i)));
            if (r > max) max = r;
            if (r < min) min = r;
        }
        double colorDistance = (max - min) / 255;

        for (int i = 0; i < spline.size() - 1; i++) {
            double r = Math.log(spline.getDistance(spline.get(i), evolute.get(i)));
            int d = new Double((max - r) / colorDistance).intValue();
            Color c = new Color(d, 0, 255 - d);
            g2.setColor(c);
            g2.drawLine(spline.get(i).x, spline.get(i).y, spline.get(i + 1).x, spline.get(i + 1).y);
        }
    }

    private PointArray renderEvolute(AkimaSpline as) {
        PointArray result = new PointArray();
        ArrayList<AkimaArc> ax = as.getArcX();
        ArrayList<AkimaArc> ay = as.getArcY();
        int seg = 11;

        for (int i = 0; i < ax.size(); i++) {
            double t = 0;
            double step = (ax.get(i).x2 - ax.get(i).x1) / seg;
            for (int j = 0; j < seg; j++) {
                result.add(as.getEvolutePoint(t, ax.get(i), ay.get(i)));
                t += step;
            }
        }

        return result;
    }

    private PointArray renderSpline(AkimaSpline as) {
        PointArray result = new PointArray();
        ArrayList<AkimaArc> ax = as.getArcX();
        ArrayList<AkimaArc> ay = as.getArcY();

        for (int i = 0; i < ax.size(); i++) {
            result.addPointArray(renderArc(ax.get(i), ay.get(i), as.getSegments()));
        }
//        if (as.isClosed()) {
//            result.addPointArray(renderArc(ax.get(ax.size() - 1), ay.get(ax.size() - 1), ax.get(0), ay.get(0), as.getSegments()));
//        }
        return result;
    }

    private PointArray renderArc(AkimaArc ax, AkimaArc ay, int segments) {
        PointArray result = new PointArray();
        double tstep = (ax.x2 - ax.x1) / segments;
        double t = 0;

        for (int i = 0; i <= segments; i++) {
            result.add(new Point(new Double(ax.k0 + t * (ax.k1 + t * (ax.k2 + t * ax.k3))).intValue(),
                                 new Double(ay.k0 + t * (ay.k1 + t * (ay.k2 + t * ay.k3))).intValue()));
            t += tstep;
        }
        return result;
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
        if ("close".equals(e.getActionCommand())) {
            if (as.isClosed()) {
                as.tear();
                closeSpline.setText("Замкнуть");
            } else {
                as.close();
                closeSpline.setText("Разомкнуть");
            }
        } else {
            points = new PointArray();
        }
        repaint();
    }
}