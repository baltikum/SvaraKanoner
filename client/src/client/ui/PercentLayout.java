package client.ui;

import java.awt.*;
import java.util.ArrayList;

/**
 * A layout that keeps it's aspect ratio and places it components centers at a given x, y and the size to a percentage
 * of the containers size.
 * All components have to be set using setConstraintsRatioByWidth if they want to be effected by this layout.
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class PercentLayout implements LayoutManager {
    private int width, height, xOffset, yOffset;

    /**
     * Internal class to keep tack of the components induvidual constraints.
     */
    private static class Constraints {
        Component component;
        float x, y, width, ratio;

        Constraints(Component component) {
            this.component = component;
        }

        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void setSize(float width, float ratio) {
            this.width = width;
            this.ratio = ratio;
        }

        /**
         * Calculates the x location of the center of the component.
         * @param parentWidth The height of the container holding the component.
         * @return The center x location.
         */
        private int getX(int parentWidth) {
            return (int)(parentWidth * x);
        }

        /**
         * Calculates the y location of the center of the component.
         * @param parentHeight The height of the container holding the component.
         * @return The center y location.
         */
        private int getY(int parentHeight) {
            return (int)(parentHeight * y);
        }

        /**
         * Calculates the size of the component given the containers width.
         * @param containerWidth The width of the container holding the component.
         * @return The dimensions of the component.
         */
        private Dimension getSize(int containerWidth) {
            Dimension result = new Dimension();
            float width = this.width * containerWidth;
            result.width = (int)width;
            result.height = (int)(width * ratio);
            return result;
        }
    }

    private final ArrayList<Constraints> children = new ArrayList<>();
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private float ratio;


    /**
     * Creates a new PercentLayout that will center in its parent and the height will be equal to width*ratio.
     * @param ratio The proportions between the width and the height.
     */
    public PercentLayout(float ratio) {
        this.ratio = ratio;
    }

    /**
     *
     * @param comp The component to effect in the container.
     * @param x The center x coordinate between 0 and 1.
     * @param y The center y coordinate between 0 and 1.
     * @param width The fraction of the parents width.
     * @param ratio The height the component will be in ratio to the width of the component.
     */
    public void setConstraintsRatioByWidth(Component comp, float x, float y, float width, float ratio) {
        Constraints constraints = null;
        for (Constraints it : children) {
            if (it.component == comp) {
                constraints = it;
                break;
            }
        }
        if (constraints == null) {
            constraints = new Constraints(comp);
            children.add(constraints);
        }
        constraints.setPosition(x, y);
        constraints.setSize(width, ratio);
        Dimension size = constraints.getSize(this.width);
        comp.setBounds(
                xOffset + constraints.getX(this.width) - size.width / 2,
                yOffset + constraints.getY(this.height) - size.height / 2,
                size.width, size.height);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        children.add(new Constraints(comp));
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).component == comp) {
                children.remove(i);
                break;
            }
        }
    }

    private void setSizes(Container parent) {
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;

        for (Constraints child : children) {
            Dimension preferredSize = child.component.getPreferredSize();
            Dimension minSize = child.component.getMinimumSize();

            preferredWidth  = Math.max(preferredWidth,  preferredSize.width);
            preferredHeight = Math.max(preferredHeight, preferredSize.height);
            minWidth = Math.max(minWidth, minSize.width);
            minHeight = Math.max(minHeight, minSize.height);
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        setSizes(parent);

        Insets insets = parent.getInsets();
        dim.width = preferredWidth + insets.left + insets.right;
        dim.height = preferredHeight + insets.top + insets.bottom;

        return dim;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = minWidth + insets.left + insets.right;
        dim.height = minHeight + insets.top + insets.bottom;

        return dim;
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int parentWidth = parent.getWidth() - (insets.left + insets.right);
        int parentHeight = parent.getHeight() - (insets.top + insets.bottom);
        xOffset = insets.left;
        yOffset = insets.top;

        width = (int)(parentHeight / ratio);
        height = (int)(parentWidth * ratio);
        if (parentWidth > width) {
            height = parentHeight;
            xOffset += (parentWidth - width) / 2;
        } else {
            width = parentWidth;
            yOffset += (parentHeight - height) / 2;
        }

        for (Constraints child : children) {
            Dimension size = child.getSize(width);
            child.component.setBounds(
                    xOffset + child.getX(width) - size.width / 2,
                    yOffset + child.getY(height) - size.height / 2,
                    size.width, size.height);
        }
    }
}
