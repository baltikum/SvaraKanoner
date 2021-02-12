package client.ui;

import client.Main;
import client.MainMenu;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/** Utility class that contains support function for the awesome components and effects.
 */
public class AwesomeUtil {

    private static Font font;
    private static float timeSinceStart = 0.0f;
    private static long lastDeltaIncrease = 0;
    private static final Set<AwesomeEffect> activeEffects = new HashSet<>();

    public static Font getFont() {
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(resourcesPath() + "GloriaHallelujah.ttf")).deriveFont(36.0f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                font = new Font(Font.SERIF, Font.BOLD, 30);
            }
        }
        return font;
    }

    public static String resourcesPath() {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static void drawBouncingText(Graphics g, Dimension dimension, String str, float fontFactor, Color color) {
        if (str.isEmpty()) return;

        char[] data = new char[str.length()];
        str.getChars(0, str.length(), data, 0);

        Font font = getFont();
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(str);
        float factor = (float)dimension.width / (float)textWidth;
        float fontSize = getFont().getSize() * factor * fontFactor;

        fontSize = Math.min(fontSize, dimension.height * fontFactor);
        g.setColor(color);
        g.setFont(font.deriveFont(fontSize));
        metrics = g.getFontMetrics();

        int descent = metrics.getDescent();
        int x = dimension.width / 2 - metrics.stringWidth(str) / 2;
        int baseY = dimension.height / 2 + (metrics.getAscent() + descent) / 2 - descent;
        for (int i = 0; i < data.length; i++) {
            int y = baseY - (int)(Math.sin((float)i * .5f + timeSinceStart * 2.0f) * dimension.height * 0.1f);
            g.drawChars(data, i, 1, x, y);
            x += g.getFontMetrics().charWidth(data[i]);
        }
    }

    public static void wiggleOnHover(AwesomeEffect.User user, float amount) {
        AwesomeEffect.create()
                .addRotationKey(amount, 100)
                .addRotationKey(-amount, 300)
                .addRotationKey(0.0f, 400)
                .addRotationKey(0.0f, 600)
                .repeats(-1).animate(user, AwesomeEffect.FOREGROUND);
        user.getEffect().pause();

        user.getComponent().addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) { }
            @Override public void mousePressed(MouseEvent e) { }
            @Override public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) {
                user.getEffect().play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                user.getEffect().pause();
            }
        });
    }

    public static void scaleOnHover(AwesomeEffect.User user, float amount) {
        AwesomeEffect.create().addScaleKey(amount, amount, 500).animate(user, AwesomeEffect.FOREGROUND);
        user.getEffect().pause();

        user.getComponent().addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) { }
            @Override public void mousePressed(MouseEvent e) { }
            @Override public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) {
                user.getEffect().setDirection(AwesomeEffect.FORWARD);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                user.getEffect().setDirection(AwesomeEffect.BACKWARDS);
            }
        });

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
        }

        for (Iterator<AwesomeEffect> it = activeEffects.iterator(); it.hasNext(); ) {
            if (it.next().update(delta)) {
                it.remove();
            }
        }
    }

    public static void register(AwesomeEffect effect) {
        activeEffects.add(effect);
    }

    public static void unregister(AwesomeEffect effect) {
        activeEffects.remove(effect);
    }

}
