package client;

import java.awt.Color;

public class PaintPoint {

    private double x;
    private double y;
    private Color color;
    private double brushSize;


    public PaintPoint(double x, double y, Color color, double brushSize) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.brushSize = brushSize;

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getFarg() {
        return color;
    }

    public double getbrushSize() {
        return brushSize;
    }

}