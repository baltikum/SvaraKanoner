package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

/** Black hovering/floating text.
 *
 */
public class AwesomeText extends JComponent implements AwesomeEffect.User {

    private String text;
    private AwesomeEffect effect;
    private Color textColor;

    public AwesomeText(String text) {
        this.text = text;
        setFont(Assets.getFont());
    }

    public String getText() {
        return text;
    }

    public void setTextColor(Color color) {
        textColor = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paintComponent(Graphics g) {
        AwesomeUtil.drawBouncingText((Graphics2D) g, effect, false, getSize(), text, getFont(), textColor, AwesomeUtil.CENTER);
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
