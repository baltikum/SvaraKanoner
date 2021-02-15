package client.ui;

import javax.swing.*;
import java.awt.*;

/** Black hovering/floating text.
 *
 */
public class AwesomeText extends JComponent implements AwesomeEffect.User {

    private int textSize = AwesomeUtil.MEDIUM_TEXT;
    private String text;
    private AwesomeEffect effect;

    public AwesomeText(String text) {
        this.text = text;
    }

    public AwesomeText(String text, int textSize) {
        this.text = text;
        this.textSize = textSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible()) return;
        AwesomeUtil.drawBouncingText((Graphics2D) g, effect, false, getSize(), text, textSize, Color.BLACK, AwesomeUtil.CENTER);
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
