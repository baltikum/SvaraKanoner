package client.ui;

import client.Assets;

import javax.swing.*;
import java.awt.*;

public class AwesomeIconLabel extends JComponent implements AwesomeEffect.User {
    private Image img;
    private String text;
    private AwesomeEffect effect;
    private Color textColor = Color.BLACK;

    public AwesomeIconLabel(Image img, String text) {
        this.img = img;
        this.text = text;

        setOpaque(false);
        setFont(Assets.getFont());
    }

    public void setTextColor(Color color) {
        textColor = color;
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
