package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {

    private Color color;
    private double lastBrushSize;

    private double brushSize;
    private double brushSizeSmall = 3.0;
    private double brushSizeNormal = 6.0;
    private double brushSizeBig = 10.0;
    private double brushSizeEraser = 30.0;
    private String selectedBrushSize ="normal";
    private String lastSelectedBrushSize;
    private ArrayList<List<PaintPoint>> paintPoints;
    private List<PaintPoint> currentPath;

    public DrawPanel() {
        super();
        colorSetup();
        color = Color.BLACK;
        brushSize = brushSizeNormal;
        paintPoints = new ArrayList<List<PaintPoint>>(25);
        addMouseListener(this);
        addMouseMotionListener(this);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (List<PaintPoint> path : paintPoints) {
            for (int i = 1; i < path.size(); i++) {
                PaintPoint pointStart = path.get(i - 1);
                PaintPoint pointEnd = path.get(i);
                Color color = path.get(i).getFarg();
                double finalBrushSize = path.get(i).getbrushSize() * getWidth();
                float finalBrushSizeFloat = (float) finalBrushSize;

                g2d.setStroke(new BasicStroke(finalBrushSizeFloat, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(color);

                double pointStartX = pointStart.getX() * getWidth();
                double pointStartY = pointStart.getY() * getHeight();
                double pointEndX = pointEnd.getX() * getWidth();
                double pointEndY = pointEnd.getY() * getHeight();
                g2d.draw(new Line2D.Double(pointStartX, pointStartY, pointEndX, pointEndY));
            }
        }
        g2d.dispose();
    }

    public void mouseDragged(MouseEvent e) {
        double xValue = e.getX();
        double xValueAdjusted = xValue / getWidth();
        double yValue = e.getY();
        double yValueAdjusted = yValue / getHeight();

        PaintPoint dragPoint = new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth());
        currentPath.add(dragPoint);
        repaint();

    }

    public void mouseClicked(MouseEvent e) {
        currentPath = new ArrayList<>();
        double xValue = e.getX();
        double xValueAdjusted = xValue / getWidth();
        double yValue = e.getY();
        double yValueAdjusted = yValue / getHeight();

        currentPath.add(new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth()));

        paintPoints.add(currentPath);
        PaintPoint dragPoint = new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth());
        currentPath.add(dragPoint);
        currentPath = null;
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        currentPath = new ArrayList<>();
        double xValue = e.getX();
        double xValueAdjusted = xValue / getWidth();
        double yValue = e.getY();
        double yValueAdjusted = yValue / getHeight();

        currentPath.add(new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth()));
        paintPoints.add(currentPath);
    }

    public void mouseReleased(MouseEvent e) {
        currentPath = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void clearPanel() {
        paintPoints.clear();
        repaint();
    }

    public void setColorGreen() {
        color = new Color(0, 204, 0);
        colorSetup();
    }

    public void setColorBlue() {
        color = Color.BLUE;
        colorSetup();
    }

    public void setColorBlack() {
        color = Color.BLACK;
        colorSetup();
    }

    public void setColorRed() {
        color = Color.RED;
        colorSetup();
    }

    public void setColorYellow() {
        color = Color.YELLOW;
        colorSetup();
    }

    public void setColorBrown() {
        color = new Color(153, 102, 0);
        colorSetup();
    }

    public void setSmallBrush() {
        selectedBrushSize = "small";
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeSmall * rescaledSize;
    }

    public void setNormalBrush() {
        selectedBrushSize = "normal";
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeNormal * rescaledSize;
    }

    public void setBigBrush() {
        selectedBrushSize = "big";
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeBig * rescaledSize;
    }

    public void setEraser() {
        if (!selectedBrushSize.equals("eraser")) {
            lastBrushSize = brushSize;
            lastSelectedBrushSize = selectedBrushSize;
        }

        selectedBrushSize = "eraser";
        color = Color.WHITE;
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeEraser * rescaledSize;

        try {
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("client\\assets\\eraser.png").getImage(),
                    new Point(0, 0), "custom cursor"));
        } catch (Exception e2) {
        }
    }

    public void updateBrushSize() {
        double width = getWidth();
        double rescaledSize = width / 878;

        if (selectedBrushSize.equals("small")) {
            brushSize = brushSizeSmall * rescaledSize;
        } else if (selectedBrushSize.equals("normal")) {
            brushSize = brushSizeNormal * rescaledSize;
        } else if (selectedBrushSize.equals("big")) {
            brushSize = brushSizeBig * rescaledSize;
        } else if (selectedBrushSize.equals("eraser")) {
            brushSize = brushSizeEraser * rescaledSize;
        }
    }

    public void colorSetup() {
        if (selectedBrushSize.equals("eraser")) {
            brushSize = lastBrushSize;
            selectedBrushSize = lastSelectedBrushSize;
        }
        try {
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("client\\assets\\paintbrush.png").getImage(),
                    new Point(0, 0), "custom cursor"));
        } catch (Exception e) {
        }
    }

}