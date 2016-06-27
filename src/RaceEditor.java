import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by dima on 28/05/16.
 *
 * for test:
 *      1 pixel = 1,321 m
 *      1 m     = 0,757 pixels
 *
 */
public class RaceEditor extends JFrame implements MouseListener, ActionListener {
    private static final long serialVersionUID = 1L;
    private PointArray points = new PointArray();
    private AkimaSpline as = new AkimaSpline(10, false, true, false);
//    private Spline as = new CubicSpline(10, false, false, false);
    private BtnPanel btnPanel;

    public RaceEditor() {
        setTitle("Редактор гонки");
        addMouseListener(this);

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                MouseEvent evt = (MouseEvent) event;
                java.awt.Point p = evt.getPoint();
                if (evt.getSource() != RaceEditor.this) {
                    p = SwingUtilities.convertPoint(evt.getComponent(), p, RaceEditor.this);
                }
                if (RaceEditor.this.getBounds().contains(p)) {
                    mouseMove(p);
                }
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        btnPanel = new BtnPanel();
        btnPanel.setBounds(0, 0, 120, 500);
        this.add(btnPanel);
        btnPanel.closeSpline.addActionListener(this);
        btnPanel.resetSpline.addActionListener(this);
        btnPanel.saveTrack.addActionListener(this);
        btnPanel.loadTrack.addActionListener(this);
        btnPanel.runRace.addActionListener(this);

        this.add(new JLabel(new ImageIcon("f1.png")));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //debugArcPaint(g);

        drawPoints(points, g);
        if (points.size() > 2) {
            drawAkimaSpline(points, g, false);
            testRunCar();
        } else if (points.size() == 2) {
            drawLine(points.get(0), points.get(1), g);
        }

    }

    private void testRunCar() {
        Driver d = new Driver();
        PointArray t = d.prepareRoute(points);
    }

    private void debugArcPaint(Graphics g) {
        Arc x = new Arc( 600, -10, -7, 1, 0,  10);
        Arc y = new Arc( 300, -20, -8, 1, 0,  10);

        PointArray pa = x.renderParametrizedArc(y, 20);
        drawCurve(pa, g, Color.blue);

        for (int i = 0; i < 10; i++) {
            double t = i;
            Point pe = x.getParametrizedEvolutePoint(t, y);
            drawPoint(pe, g);

            int r = x.getParametrizedRadius(y, t).intValue();
            g.setColor(Color.red);
            drawCircle(pe, r, g);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawCircle(Point center, int radius, Graphics g) {
        int x = new Double(center.x).intValue() - radius;
        int y = new Double(center.y).intValue() - radius;

        g.drawOval(x, y, 2 * radius, 2 * radius);
    }

    private void drawLine(Point p1, Point p2, Graphics g) {
        int x1 = new Double(p1.x).intValue();
        int x2 = new Double(p2.x).intValue();
        int y1 = new Double(p1.y).intValue();
        int y2 = new Double(p2.y).intValue();
        g.drawLine(x1, y1, x2, y2);
    }

    private void drawPoint(Point p, Graphics g) {
        int x = new Double(p.x).intValue();
        int y = new Double(p.y).intValue();
        g.drawOval(x - 3, y - 3, 5, 5);
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println(e.getX() + ", " + e.getY());
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
        as.buildSpline(points);
        PointArray spline = as.renderSpline();
        drawCurve(spline, g, Color.black);
        PointArray evolute = as.renderParametrizedEvolute();
        drawColored2(spline, as.renderRadius(), g);
//        drawCircles(as.renderRadius(), evolute, spline, g, new Color(0, 128, 0));
//        drawColored(spline, evolute, g);
        //drawCurve(evolute, g, new Color(0, 128, 0));
//        Driver d = new Driver();
//        PointArray localMax = d.prepareRoute(spline, evolute);
//        drawPoints(localMax, g, new Color(0, 128, 0));
    }

    private void drawCircles(ArrayList<Double> radius, PointArray evolute, PointArray s, Graphics g, Color c) {
        for (int i = 0; i < evolute.size(); i++) {
            if (i == 12) {
                g.setColor(c);

                Double r1 = Math.abs(radius.get(i));
                Double r2 = s.get(i).getDistanceTo(evolute.get(i));

                int x1 = new Double(evolute.get(i).x - r1).intValue();
                int y1 = new Double(evolute.get(i).y - r1).intValue();
                int w1 = 2 * r1.intValue();
                int h1 = 2 * r1.intValue();

                int x2 = new Double(evolute.get(i).x - r2).intValue();
                int y2 = new Double(evolute.get(i).y - r2).intValue();
                int w2 = 2 * r2.intValue();
                int h2 = 2 * r2.intValue();

                g.drawOval(x1, y1, w1, h1);
                drawPoint(evolute.get(i), g);
                g.setColor(Color.red);
                g.drawOval(x2, y2, w2, h2);
                System.out.println(r1);
            }
        }
    }

    private void drawColored2(PointArray spline, ArrayList<Double> radius, Graphics g) {
        //find max and min
        double max = 0;
        double min = 1000000;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

//        System.out.println(spline.size());
//        System.out.println(radius.size());

        for (int i = 0; i < spline.size(); i++) {
            double r = Math.log(radius.get(i));
            if (r > max) max = r;
            if (r < min) min = r;

       //     System.out.println(radius.get(i));
        }
        double colorDistance = (max - min) / 255;

        for (int i = 0; i < spline.size() - 1; i++) {
            double r = Math.log(radius.get(i));
            int d = new Double((max - r) / colorDistance).intValue();
            Color c = new Color(d, 0, 255 - d);
            g2.setColor(c);
            drawLine(spline.get(i), spline.get(i + 1), g);
        }

    }

    private void drawColored(PointArray spline, PointArray evolute, Graphics g) {
        //find max and min
        double max = 0;
        double min = 1000000;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        System.out.println("evolute");

        for (int i = 0; i < spline.size(); i++) {
            double r = Math.log(spline.get(i).getDistanceTo(evolute.get(i)));
            if (r > max) max = r;
            if (r < min) min = r;
            System.out.println(spline.get(i).getDistanceTo(evolute.get(i)));
        }
        double colorDistance = (max - min) / 255;

        for (int i = 0; i < spline.size() - 1; i++) {
            double r = Math.log(spline.get(i).getDistanceTo(evolute.get(i)));
            int d = new Double((max - r) / colorDistance).intValue();
            Color c = new Color(d, 0, 255 - d);
            g2.setColor(c);
            drawLine(spline.get(i), spline.get(i + 1), g);
        }
    }

    private void drawCurve (PointArray p, Graphics g, Color c) {
        g.setColor(c);
        for (int i = 0; i < p.size() - 1; i++) {
            drawLine(p.get(i), p.get(i + 1), g);
        }
    }

    private void drawPoints (PointArray p, Graphics g) {
        g.setColor(Color.black);
        for (int i = 0; i < p.size(); i++) drawPoint(p.get(i), g);

        if (p.getActive() >= 0) {
            g.setColor(Color.red);
            drawPoint(p.get(p.getActive()), g);
        }

    }

    private void drawPoints (PointArray p, Graphics g, Color c) {
        g.setColor(c);
        for (int i = 0; i < p.size(); i++) drawPoint(p.get(i), g);
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
                (cx * cx + cy * cy) * (ay - by)) / d + pa.x).intValue();
        int oy = new Double(((ax * ax + ay * ay) * (cx - bx) +
                (bx * bx + by * by) * (ax - cx) +
                (cx * cx + cy * cy) * (bx - ax)) / d + pa.y).intValue();
        return new Point(ox, oy);
    }

    private void drawCircles (PointArray points, Graphics g) {
        Point prev = null;
        for (int i = 0; i < points.size() - 2; i++) {
            g.setColor(Color.magenta);
            Point p = circleCenter(points.get(i), points.get(i + 1), points.get(i + 2));
            drawPoint(p, g);

            if (prev != null) {
                drawLine(p, prev, g);
            }
            prev = p;
        }
    }

    private void mouseMove(java.awt.Point p) {
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
        switch (e.getActionCommand()) {
            case "close" :
                if (as.isClosed()) {
                    as.tear();
                    btnPanel.closeSpline.setText("Замкнуть");
                } else {
                    as.close();
                    btnPanel.closeSpline.setText("Разомкнуть");
                }
                break;
            case "reset" :
                points = new PointArray();
                break;
            case "save" :
                points.save();
                break;
            case "load" :
                points.load();
                break;
            case "run" :
                run();
        }
        repaint();
    }

    private void run() {

    }
}