package client.ui;

import javax.swing.*;
import java.awt.*;

/** Black hovering/floating text.
 *
 */
public class AwesomeText extends JComponent implements AwesomeEffect.User {

    private static Font font;
    private String text;
    private AwesomeEffect effect;

    public AwesomeText(String text) {
        this.text = text;
        setFont(AwesomeUtil.getFont());
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible()) return;
        if (text == null) return;
        if (effect != null) {
            effect.paint((Graphics2D)g, text, 1.0f, Color.BLACK, getSize());
        } else {
            AwesomeUtil.drawBouncingText(g, getSize(), text, 1.0f, Color.BLACK);
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
