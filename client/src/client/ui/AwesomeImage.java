package client.ui;

import java.awt.*;

public class AwesomeImage extends Component implements AwesomeEffect.User {
    private AwesomeEffect effect;
    public Image image;

    public AwesomeImage(Image img) {
        image = img;
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible()) return;
        AwesomeUtil.drawImage((Graphics2D) g, effect, true, getSize(), image);
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
