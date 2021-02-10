package client.ui;

import javax.swing.*;
import java.awt.*;

public class AwesomeButton extends JButton implements AwesomeEffect.User {

    private AwesomeEffect effect;
    private float fontFactor = 1.0f;
    private float delta = 0.0f;
    private Image background;

    public AwesomeButton(String text) {
        super(text);
        setFont(AwesomeUtil.getFont());

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentAreaFilled(false);
    }

    public AwesomeButton(String text, Image background) {
        super(text);
        setFont(AwesomeUtil.getFont());

        setBorder(BorderFactory.createEmptyBorder());
        setBorderPainted(false);
        setContentAreaFilled(false);

        this.background = background;
    }

    public void setFontFactor(float value) {
        fontFactor = value;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (effect != null) {
            effect.transform((Graphics2D) g);
        }

        if (this.background != null) {
            g.drawImage(background, 0, 0,
                    getWidth(), getHeight(), null);
        }

        AwesomeUtil.drawBouncingText(g, this, getText(), fontFactor);
    }

    public void setEffect(AwesomeEffect effect) {
        this.effect = effect;
    }
}
