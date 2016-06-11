import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by dima on 28/05/16.
 */
public class RaceEditor extends JFrame implements MouseListener, ActionListener {
    private static final long serialVersionUID = 1L;
    private PointArray points = new PointArray();
    private AkimaSpline as = new AkimaSpline(10, false, true, false);
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

        drawPoints(points, g);
        if (points.size() > 2) {
            drawAkimaSpline(points, g, false);
        } else if (points.size() == 2) {
            drawLine(points.get(0), points.get(1), g);
        }
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
        PointArray evolute = as.renderEvolute();
        drawColored(spline, evolute, g);
        //drawCurve(evolute, g, new Color(0, 128, 0));
        Driver d = new Driver();
        PointArray localMax = d.prepareRoute(spline, evolute);
        drawPoints(localMax, g, new Color(0, 128, 0));
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

    public void mouseMove(java.awt.Point p) {
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
                save();
                break;
            case "load" :
                load();
                break;
            case "run" :
                run();
        }
        repaint();
    }

    private void save() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("test.track");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(points);
            oos.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void load() {
        FileInputStream fis;
        try {
            fis = new FileInputStream("test.track");
            ObjectInputStream ois = new ObjectInputStream(fis);
            points = (PointArray) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void run() {

    }
}