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

/*        g.setColor(Color.black);
        for (int i = 0; i < points.size(); i++) {
            g.drawOval(points.get(i).x - 3, points.get(i).y - 3, 5, 5);
        }

        if (points.getActive() >= 0) {
            g.setColor(Color.red);
            g.drawOval(points.get(points.getActive()).x - 3, points.get(points.getActive()).y - 3, 5, 5);
            g.setColor(Color.black);
        }
*/
        if (points.size() > 2) {
            drawAkimaSpline(points, g);
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

    private void drawAkimaSpline (PointArray points, Graphics g) {
        PointArray extendedPoints = extendPoints(points);
        PointArray spline = AkimaSpline.plotSpline(extendedPoints);

        drawPoints(extendedPoints, g);
    //    drawCurve(spline, g, Color.black);

        // curve, parametrised by sequence:
        drawParametrizedCurve(points, true, g, Color.blue);

        // curve, parametrised by sequence:
     //   drawParametrizedCurve(points, false, g, Color.red);

    }

    private void drawParametrizedCurve (PointArray source, boolean divideBySeqNo, Graphics g, Color c) {
        PointArray tx = new PointArray();
        PointArray ty = new PointArray();
        dividePoints(source, tx, ty, divideBySeqNo);

        PointArray paramSplineSeq = mergePoints(produceSpline(tx), produceSpline(ty));
        drawCurve(paramSplineSeq, g, c);
    }

    private void dividePoints(PointArray xy, PointArray tx, PointArray ty, boolean divideBySeqNo) {
        int nextT = 0;
        for (int i = 0; i < xy.size(); i++) {
            tx.add(new Point(nextT, xy.get(i).x));
            ty.add(new Point(nextT, xy.get(i).y));

            if (divideBySeqNo) {
                nextT = i + 1;
            } else {
                if (i < xy.size() - 1) nextT = new Double(xy.getDistance(xy.get(i), xy.get(i + 1))).intValue();
            }
        }
    }

    private PointArray mergePoints (PointArray tx, PointArray ty) {
        PointArray merged = new PointArray();
        for (int i = 0; i < tx.size(); i++) {
           merged.add(new Point(tx.get(i).y, ty.get(i).y));
        }
        return  merged;
    }

    private PointArray produceSpline (PointArray source) {
        PointArray extendedPoints = extendPoints(source);
        PointArray spline = AkimaSpline.plotSpline(extendedPoints);
        return spline;
    }

    private PointArray extendPoints (PointArray source) {
        PointArray extendedPoints = new PointArray();
        extendedPoints.copyFrom(source);
        AkimaSpline.addExtraPoints(extendedPoints);
        return extendedPoints;
    }

    private void drawCurve (PointArray p, Graphics g, Color c) {
        g.setColor(c);
        for (int i = 0; i < p.size() - 1; i++) {
            g.drawLine(p.get(i).x, p.get(i).y, p.get(i + 1).x, p.get(i + 1).y);
        }
    }

    private void drawPoints (PointArray p, Graphics g) {
        for (int i = 0; i < p.size(); i++) {
            if (i == 0 || i == 1 || i == p.size() - 1 || i == p.size() - 2) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.black);
            }
            g.drawOval(p.get(i).x - 3, p.get(i).y - 3, 5, 5);
        }

        if (p.getActive() >= 0) {
            g.setColor(Color.red);
            g.drawOval(p.get(p.getActive()).x - 3, p.get(p.getActive()).y - 3, 5, 5);
            g.setColor(Color.black);
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