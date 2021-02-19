package client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A component that renders a image only.
 * Can be used with AwesomeEffect.
 *
 * @author Jesper Jansson
 * @version 07/02/21
 */
public class AwesomeImage extends JComponent implements AwesomeEffect.User {
    private AwesomeEffect effect;
    public Image image;

    /**
     * Creates a component that only displays an image, scaled to fit the size of the component.
     * @param img
     */
    public AwesomeImage(Image img) {
        image = img;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!isVisible()) return;
        AwesomeUtil.drawImage((Graphics2D) g, effect, getSize(), image);
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
