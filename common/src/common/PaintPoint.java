package common;
import java.awt.Color;
import java.io.Serializable;


    /**
     * A class for saving the X and Y values,
     * the color and the brushSize for each point.
     *
     * @author Johnny Larsson
     */


public class PaintPoint implements Serializable {
    private double x;
    private double y;
    private Color color;
    private double brushSize;


        /**
         * Constructor for PaintPoint
         * @param x The x value for that point
         * @param x The y value for that point
         * @param x The color for that point
         * @param x The brush size  for that point
         */
    public PaintPoint(double x, double y, Color color, double brushSize) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.brushSize = brushSize;
    }


        /**
         * @return The x value of that point
         */
    public double getX() {
        return x;
    }

        /**
         * @return The y value of that point
         */
    public double getY() {
        return y;
    }


        /**
         * @return The color of that point
         */
    public Color getColor() {
        return color;
    }


        /**
         * @return The brush size of that point
         */
    public double getBrushSize() {
        return brushSize;
    }

}