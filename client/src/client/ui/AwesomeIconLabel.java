package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

/**
 * Similar to swings JLabel, but using bouncing text and can only display the icon on the left side.
 * Can be used with AwesomeEffect.
 *
 * @author Jesper Jansson
 * @version 11/02/21
 */
public class AwesomeIconLabel extends JComponent implements AwesomeEffect.User {
    private Image img;
    private String text;
    private AwesomeEffect effect;
    private Color textColor = Color.BLACK;

    /**
     * Creates a component with the given icon and text.
     * @param img The icon to render of the left side of the text.
     * @param text The text to render.
     */
    public AwesomeIconLabel(Image img, String text) {
        this.img = img;
        this.text = text;

        setOpaque(false);
        setFont(Assets.getFont());
    }

    /**
     * Sets the color of the text.
     * @param color The color......
     */
    public void setTextColor(Color color) {
        textColor = color;
    }

    /**
     * Sets the text to be displayed
     * @param text The text?!
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paintComponent(Graphics g) {
        Dimension iconDim = getSize();
        Dimension textDim = getSize();
        iconDim.width = getHeight();
        textDim.width -= getHeight();
        AwesomeUtil.drawTextAndIcon((Graphics2D) g, effect, textDim, text, getFont(), textColor, img, iconDim, AwesomeUtil.LEFT);
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
}
