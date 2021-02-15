package client.ui;

import javax.swing.*;
import java.awt.*;

public class AwesomeIconLabel extends JComponent implements AwesomeEffect.User {
    private Image img;
    private String text;
    private AwesomeEffect effect;
    private float radians = 0.0f;

    public AwesomeIconLabel(Image img, String text) {
        this.img = img;
        this.text = text;
    }

    public void setRotation(float degrees) {
        radians = (float)Math.toRadians(degrees);
    }

    @Override
    public void paint(Graphics g) {
        Dimension iconDim = getSize();
        Dimension textDim = getSize();
        iconDim.width = getHeight();
        textDim.width -= getHeight();
        AwesomeUtil.drawTextAndIcon((Graphics2D) g, effect, textDim, text, AwesomeUtil.MEDIUM_TEXT, Color.BLACK, img, iconDim, AwesomeUtil.LEFT);
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
