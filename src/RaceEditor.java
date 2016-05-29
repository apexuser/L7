import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Created by dima on 28/05/16.
 */
public class RaceEditor extends JFrame implements MouseListener {
    private static final long serialVersionUID = 1L;
    private PointArray points = new PointArray();

    public RaceEditor() {
        setTitle("Редактор гонки");
        addMouseListener(this);

     /*   points.add(new Point(100, 100));
        points.add(new Point(150, 120));
        points.add(new Point(200, 110));
        points.add(new Point(250, 130));
        points.add(new Point(300, 200));
        points.add(new Point(350, 120));
        points.add(new Point(400, 140));
        points.add(new Point(450, 130));
        points.add(new Point(500, 150));
        points.add(new Point(550, 140));
        points.add(new Point(600, 160));*/

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
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.black);
        for (int i = 0; i < points.size(); i++) {
            g.drawOval(points.get(i).x - 3, points.get(i).y - 3, 5, 5);
        }

        if (points.getActive() >= 0) {
            g.setColor(Color.red);
            g.drawOval(points.get(points.getActive()).x - 3, points.get(points.getActive()).y - 3, 5, 5);
            g.setColor(Color.black);
        }

        if (points.size() > 5) {
            PointArray spline = AkimaSpline.plotSpline(points);

            for (int i = 0; i < spline.size() - 1; i++) {
                g.drawLine(spline.get(i).x, spline.get(i).y, spline.get(i + 1).x, spline.get(i + 1).y);
            }
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
}