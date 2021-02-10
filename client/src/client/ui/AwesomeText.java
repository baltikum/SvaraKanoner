package client.ui;

import javax.swing.*;
import java.awt.*;

public class AwesomeText extends JLabel {

    private static Font font;

    private AwesomeEffect textEffect;

    public AwesomeText(String text) {
        super(text);
        setFont(AwesomeUtil.getFont());
    }

    public AwesomeText(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    @Override
    public void paintComponent(Graphics g) {
        AwesomeUtil.drawBouncingText(g, this, getText(),1.0f);
    }

    public void setTextEffect(AwesomeEffect effect) {
        this.textEffect = effect;
    }
}
