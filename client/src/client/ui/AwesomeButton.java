package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

/** A button with text and/or a background. The text is red and floating.
 *
 */
public class AwesomeButton extends JButton implements AwesomeEffect.User {

    private AwesomeEffect effect;
    private Image background;

    public AwesomeButton(String text) {
        this(text, null);
    }

    public AwesomeButton(Image background) {
        this(null, background);
    }

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
