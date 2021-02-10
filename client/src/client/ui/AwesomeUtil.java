package client.ui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AwesomeUtil {

    private static Font font;
    private static float timeSinceStart = 0.0f;
    private static long lastDeltaIncrease = 0;
    private static ArrayList<AwesomeEffect> activeEffects = new ArrayList<>();

    public static Font getFont() {
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File("c:/users/Spankarn/dropbox/data/dat055/GloriaHallelujah.ttf")).deriveFont(36.0f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                font = new Font(Font.SERIF, Font.BOLD, 30);
            }
        }
        return font;
    }

    public static void drawBouncingText(Graphics g, Component component, String str, float fontFactor) {
        if (str.isEmpty()) return;

        int width = component.getWidth();
        int height = component.getHeight();
        char[] data = new char[str.length()];
        str.getChars(0, str.length(), data, 0);

        g.setFont(getFont());
        g.setColor(Color.RED);

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(str);
        float factor = (float)width / (float)textWidth;
        float fontSize = getFont().getSize() * factor * fontFactor;

        fontSize = Math.min(fontSize, height * fontFactor);
        g.setFont(font.deriveFont(fontSize));
        metrics = g.getFontMetrics();

        int ascent = metrics.getAscent();
        int descent = metrics.getDescent();

        int x = (width - metrics.stringWidth(str)) / 2;
        int baseY = height / 2 + (ascent + descent) / 2 - descent;
        for (int i = 0; i < data.length; i++) {
            int y = baseY - (int)(Math.sin((float)x + timeSinceStart) * 8.0);
            g.drawChars(data, i, 1, x, y);
            x += g.getFontMetrics().charWidth(data[i]);
        }
    }

    public static void increaseDelta() {
        int delta = 1000 / 20;
        if (lastDeltaIncrease == 0) {
            lastDeltaIncrease = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            delta = (int)(currentTime - lastDeltaIncrease);
            timeSinceStart += (float)delta / 1000.0f;
            lastDeltaIncrease = currentTime;
            System.out.println(delta);
        }

        for (AwesomeEffect effect : activeEffects) {
            effect.update(delta);
        }
    }

    public static void register(AwesomeEffect effect) {
        activeEffects.add(effect);
    }

}
