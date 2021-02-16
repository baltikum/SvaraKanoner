package client.ui;

import javax.swing.*;
import java.awt.*;

/** A button with text and/or a background. The text is red and floating.
 *
 */
public class AwesomeButton extends JButton implements AwesomeEffect.User {

    private int textSize;
    private AwesomeEffect effect;
    private Image background;

    public AwesomeButton(String text, int textSize) {
        this(text, null, textSize);
    }

    public AwesomeButton(Image background) {
        this(null, background, AwesomeUtil.MEDIUM_TEXT);
    }

    public AwesomeButton(String text, Image background, int textSize) {
        super(text);

        this.background = background;
        this.textSize = textSize;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setTextSize(int size) {
        textSize = size;
    }

    public void setBackground(Image img) {
        background = img;
    }

    @Override
    public void paintComponent(Graphics g) {
        AwesomeUtil.drawTextAndBackground((Graphics2D) g, effect, getSize(), getText(), textSize, Color.RED, background, AwesomeUtil.CENTER);
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
