package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

/**
 * A component that renders text only in the bouncy format.
 * Can be used with AwesomeEffect.
 *
 * @author Jesper Jansson
 * @version 07/02/21
 */
public class AwesomeText extends JComponent implements AwesomeEffect.User {

    private String text;
    private AwesomeEffect effect;
    private Color textColor;

    /**
     * Creates an instance of the component with default font Assets.getFont().
     * @param text The text to be rendered.
     */
    public AwesomeText(String text) {
        this.text = text;
        setFont(Assets.getFont());
    }

    /**
     *
     * @return The text currently rendered by the component.
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param color The color that the text should be rendered with.
     */
    public void setTextColor(Color color) {
        textColor = color;
    }

    /**
     *
     * @param text The new string to be rendered.
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paintComponent(Graphics g) {
        AwesomeUtil.drawBouncingText((Graphics2D) g, effect, getSize(), text, getFont(), textColor, AwesomeUtil.CENTER);
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
