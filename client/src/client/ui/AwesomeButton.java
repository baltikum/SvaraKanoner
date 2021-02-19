package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

/** A button with text and/or a background. The text is drawn bouncing.
 * The text is drawn in the center of the button and the background scales to fit.
 * It can also be used with the AwesomeEffect class.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class AwesomeButton extends JButton implements AwesomeEffect.User {

    private AwesomeEffect effect;
    private Image background;

    /**
     * Creates a button with text and no background.
     * @param text The text to be rendered on the button.
     */
    public AwesomeButton(String text) {
        this(text, null);
    }

    /**
     * Creates a button with a background image only.
     * @param background
     */
    public AwesomeButton(Image background) {
        this(null, background);
    }

    /**
     * Creates a button with a background image and text.
     * @param text The text to be rendered.
     * @param background The background image to be scaled and rendered.
     */
    public AwesomeButton(String text, Image background) {
        super(text);

        this.background = background;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(Assets.getFont());
    }

    /**
     * Replaces the background image with a new one, or removes it if img is null.
     * @param img The new background or null.
     */
    public void setBackground(Image img) {
        background = img;
    }

    @Override
    public void paintComponent(Graphics g) {
        AwesomeUtil.drawTextAndBackground((Graphics2D) g, effect, getSize(), getText(), getFont(), Color.RED, background, AwesomeUtil.CENTER);
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
