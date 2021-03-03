package client;

import client.ui.AwesomeEffect;
import client.ui.AwesomeUtil;
import common.PaintPoint;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;



    /**
     * A class for with a JPanel for drawing.
     *
     *
     * @author Johnny Larsson
     */



public class DrawPanel extends JPanel implements Serializable, MouseListener, MouseMotionListener, AwesomeEffect.User {

    private Color color;
    private double lastBrushSize;
    private AwesomeEffect effect = null;

    private double brushSize;
    private double brushSizeSmall = 3.0;
    private double brushSizeNormal = 6.0;
    private double brushSizeBig = 10.0;
    private double brushSizeEraser = 30.0;
    private Color lastColor;
    private String selectedBrushSize ="normal";
    private String lastSelectedBrushSize;
    private ArrayList<List<PaintPoint>> paintPoints;
    private List<PaintPoint> currentPath;
    private boolean canEdit = true;

    public DrawPanel() {
        super();
        colorSetup();
        color = Color.BLACK;
        brushSize = brushSizeNormal;
        paintPoints = new ArrayList<List<PaintPoint>>(25);
        addMouseListener(this);
        addMouseMotionListener(this);

    }


    public DrawPanel(ArrayList<List<PaintPoint>> paintPoints) {
        super();
        setBackground(Color.WHITE);
        this.paintPoints = paintPoints;
    }

    public void setDrawData(ArrayList<List<PaintPoint>> paintPoints) {
        this.paintPoints = paintPoints;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (paintPoints != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (effect != null) {
                effect.transform(g2d, getSize(), true);
            }

            for (List<PaintPoint> path : paintPoints) {
                for (int i = 1; i < path.size(); i++) {
                    PaintPoint pointStart = path.get(i - 1);
                    PaintPoint pointEnd = path.get(i);
                    Color color = path.get(i).getColor();
                    double finalBrushSize = path.get(i).getBrushSize() * getWidth();
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
    }

    public synchronized void mouseDragged(MouseEvent e) {
            if (canEdit && paintPoints != null && (SwingUtilities.isLeftMouseButton(e)) ) {
                double xValue = e.getX();
                double xValueAdjusted = xValue / getWidth();
                double yValue = e.getY();
                double yValueAdjusted = yValue / getHeight();

                PaintPoint dragPoint = new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth());
                currentPath.add(dragPoint);
                repaint();
            }
    }

    public synchronized void mouseClicked(MouseEvent e) {
        if (canEdit && (SwingUtilities.isLeftMouseButton(e))) {
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
    }

    public synchronized void mousePressed(MouseEvent e) {
        if (canEdit && (SwingUtilities.isLeftMouseButton(e))) {
            currentPath = new ArrayList<>();
            double xValue = e.getX();
            double xValueAdjusted = xValue / getWidth();
            double yValue = e.getY();
            double yValueAdjusted = yValue / getHeight();

            currentPath.add(new PaintPoint(xValueAdjusted, yValueAdjusted, color, brushSize / getWidth()));
            paintPoints.add(currentPath);
        }
    }

    public synchronized void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            currentPath = null;
        }
    }

    @Override
    public synchronized void mouseEntered(MouseEvent e) {
    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
    }

    @Override
    public synchronized void mouseExited(MouseEvent e) {
    }

    public void clearPanel() {
        if (canEdit) {
            paintPoints.clear();
            repaint();
        }
    }

    public void setColor(String colorToSet){
        switch(colorToSet)
        {
            case "GREEN" -> { color = new Color(0, 204, 0);
            }
            case "BLUE"-> { color = Color.BLUE;
            }
            case "BLACK"-> { color = Color.BLACK;
            }
            case "RED"-> { color = Color.RED;
            }
            case "YELLOW"-> { color = Color.YELLOW;
            }
            case "BROWN"-> { color = new Color(153, 102, 0);
            }
            case "PINK"-> { color = Color.PINK;
            }
            case "ORANGE"-> { color = Color.ORANGE;
            }
            case "GRAY"->{ color = Color.GRAY;
            }
        }
        colorSetup();
    }

    public void setSmallBrush() {
        brushSetup();
        selectedBrushSize = "small";
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeSmall * rescaledSize;
    }

    public void setNormalBrush() {
        brushSetup();
        selectedBrushSize = "normal";
        double width = getWidth();
        double rescaledSize = width / 878;
        brushSize = brushSizeNormal * rescaledSize;
    }

    public void setBigBrush() {
        brushSetup();
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
        lastColor = color;
        try {
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("client\\assets\\paintbrush.png").getImage(),
                    new Point(0, 0), "custom cursor"));
        } catch (Exception e) {
        }
    }

    public void brushSetup(){
        color = lastColor;
        try {
            setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("client\\assets\\paintbrush.png").getImage(),
                    new Point(0, 0), "custom cursor"));
        } catch (Exception e) {
        }
    }

    @Override
    public void setEffect(AwesomeEffect effect) {
        AwesomeUtil.register(this, effect);
        this.effect = effect;
    }

    @Override
    public AwesomeEffect getEffect() {
        return effect;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public synchronized ArrayList<List<PaintPoint>> getPictureAndStopPainting() {
        canEdit = false;
        return paintPoints;
    }
}
