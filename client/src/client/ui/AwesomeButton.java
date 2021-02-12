package client.ui;

import javax.swing.*;
import java.awt.*;

/** A button with text and/or a background. The text is red and floating.
 *
 */
public class AwesomeButton extends JButton implements AwesomeEffect.User {

    private AwesomeEffect effect;
    private float fontFactor = 1.0f;
    private final Image background;

    public AwesomeButton(String text) {
        this(text, null);
    }

    public AwesomeButton(String text, Image background) {
        super(text);
        setFont(AwesomeUtil.getFont());

        this.background = background;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setFontFactor(float value) {
        fontFactor = value;
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible()) return;
        String text = getText();
        if (effect != null) {
            effect.paint((Graphics2D) g, background, text, fontFactor, Color.RED, getSize());
        } else {
            if (background != null) g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            if (text != null) AwesomeUtil.drawBouncingText(g, getSize(), text, fontFactor, Color.RED);
        }
    }

    @Override
    public void setEffect(AwesomeEffect effect) {
        AwesomeUtil.register(effect);
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
