package client.ui;

import java.awt.*;
import java.util.ArrayList;

public class PercentLayout implements LayoutManager {
    private int width, height, xOffset, yOffset;

    public static class Constraints {
        Component component;
        float x, y, width, ratio;

        Constraints(Component component) {
            this.component = component;
        }

        public Constraints setPosition(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Constraints setSize(float width, float ratio) {
            this.width = width;
            this.ratio = ratio;
            return this;
        }

        private int getX(int parentWidth) {
            return (int)(parentWidth * x);
        }

        private int getY(int parentHeight) {
            return (int)(parentHeight * y);
        }

        private Dimension getSize(int containerWidth) {
            Dimension result = new Dimension();
            float width = this.width * containerWidth;
            result.width = (int)width;
            result.height = (int)(width * ratio);
            return result;
        }
    }

    private ArrayList<Constraints> children = new ArrayList<>();
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;
    private float ratio;

    public PercentLayout(float ratio) {
        this.ratio = ratio;
    }

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
        sizeUnknown = false;

        return dim;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = minWidth + insets.left + insets.right;
        dim.height = minHeight + insets.top + insets.bottom;
        sizeUnknown = false;

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
