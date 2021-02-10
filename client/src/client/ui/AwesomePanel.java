package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AwesomePanel extends JPanel implements ComponentListener {
    private static final int X_OFFSET = 0;
    private static final int Y_OFFSET = 1;
    private static final int WIDTH_OFFSET = 2;
    private static final int HEIGHT_OFFSET = 3;

    private float[] values;

    public AwesomePanel() {
        addComponentListener(this);
    }

    public void add(Component component, float x, float y, float width, float height) {
        super.add(component);

        int length = 0;
        if (values != null) {
            length = values.length;
            float[] newValues = new float[length + 4];
            System.arraycopy(values, 0, newValues, 0, length);
            values = newValues;
        } else {
            values = new float[4];
        }

        values[length + X_OFFSET]      = x;
        values[length + Y_OFFSET]      = y;
        values[length + WIDTH_OFFSET]  = width;
        values[length + HEIGHT_OFFSET] = height;
    }

    @Override
    public Component add(Component component) { throw new UnsupportedOperationException(); }
    @Override
    public Component add(Component component, int index) { throw new UnsupportedOperationException(); }
    @Override
    public void add(Component component, Object constraints) { throw new UnsupportedOperationException(); }
    @Override
    public void add(Component component, Object constraints, int index) { throw new UnsupportedOperationException(); }
    @Override
    public Component add(String name, Component component) { throw new UnsupportedOperationException(); }


    @Override
    public void componentResized(ComponentEvent e) {
        float parentWidth = getWidth();
        float parentHeight = getHeight();

        Component[] components = getComponents();
        System.out.println(values.length);
        System.out.println(components.length);
        for (int index = 0, valueIndex = 0; index < components.length; index++, valueIndex += 4) {
            float percentX = values[valueIndex + X_OFFSET];
            float percentY = values[valueIndex + Y_OFFSET];
            float percentWidth = values[valueIndex + WIDTH_OFFSET];
            float percentHeight = values[valueIndex + HEIGHT_OFFSET];

            Component component = getComponent(index);
            int x = (int)(parentWidth * percentX);
            int y = (int)(parentHeight * percentY);
            int width = (int)(parentWidth * percentWidth);
            int height = (int)(parentHeight * percentHeight);
            component.setBounds(x, y, width, height);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) { }
    @Override
    public void componentShown(ComponentEvent e) { }
    @Override
    public void componentHidden(ComponentEvent e) { }
}
