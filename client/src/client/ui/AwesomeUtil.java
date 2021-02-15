package client.ui;

import client.Main;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;

/** Utility class that contains support function for the awesome components and effects.
 */
public class AwesomeUtil {

    public static final int SMALL_TEXT  = 0;
    public static final int MEDIUM_TEXT = 1;
    public static final int BIG_TEXT    = 2;

    public static final int LEFT = 1;
    public static final int CENTER = 2;


    private static Font font;
    private static final Font[] sizedFonts = new Font[3];
    private static float timeSinceStart = 0.0f;
    private static long lastDeltaIncrease = 0;
    private static final Set<AwesomeEffect> activeEffects = new HashSet<>();

    private static void loadFont() {
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(resourcesPath() + "GloriaHallelujah.ttf"));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                font = new Font(Font.SERIF, Font.BOLD, 30);
            }
        }
    }

    public static Font getFont(int textSize) {
        if (font == null) {
            loadFont();
            updateFonts(500);
        }
        return sizedFonts[textSize];
    }

    public static void updateFonts(int screenWidth) {
        if (font == null) loadFont();
        sizedFonts[SMALL_TEXT]  = font.deriveFont(screenWidth * .02f);
        sizedFonts[MEDIUM_TEXT] = font.deriveFont(screenWidth * .05f);
        sizedFonts[BIG_TEXT]    = font.deriveFont(screenWidth * .1f);
    }

    public static String resourcesPath() {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static boolean drawBouncingText(Graphics2D g, AwesomeEffect effect, boolean shouldTransform, Dimension dimension, String str, int textSize, Color color, int alignment) {
        g.setClip(null);
        if (str.isEmpty()) return false;

        char[] data = new char[str.length()];
        str.getChars(0, str.length(), data, 0);

        g.setColor(color);
        g.setFont(sizedFonts[textSize]);
        FontMetrics metrics = g.getFontMetrics();

        boolean isTransformed = false;
        if (effect != null && shouldTransform) {
            effect.transform(g, dimension);
            isTransformed = true;
        }

        int descent = metrics.getDescent();
        int x = 0;
        if (alignment == CENTER) {
            x = dimension.width / 2 - metrics.stringWidth(str) / 2;
        }
        int baseY = dimension.height / 2 + (metrics.getAscent() + descent) / 2 - descent;
        for (int i = 0; i < data.length; i++) {
            int y = baseY - (int)(Math.sin((float)i * .5f + timeSinceStart * 2.0f) * dimension.height * 0.1f);
            g.drawChars(data, i, 1, x, y);
            x += g.getFontMetrics().charWidth(data[i]);
        }

        return isTransformed;
    }

    public static boolean drawImage(Graphics2D g, AwesomeEffect effect, boolean shouldTransform, Dimension dim, Image img) {
        if (img == null) return false;
        boolean isTransformed = false;
        if (effect != null && shouldTransform) {
            effect.transform(g, dim);
            isTransformed = true;
        }
        Image sprite = effect != null ? effect.getSprite() : null;
        g.drawImage(sprite != null ? sprite : img, 0, 0, dim.width, dim.height, null);
        return isTransformed;
    }

    public static void drawTextAndBackground(Graphics2D g, AwesomeEffect effect, Dimension dim, String text, int textSize, Color color, Image background, int textAlignment) {
        if (effect == null) {
            drawImage(g, null, false, dim, background);
            drawBouncingText(g, null, false, dim, text, textSize, color, textAlignment);
        } else {
            int effects = effect.getEffects();
            AffineTransform savedState = effects == AwesomeEffect.BACKGROUND ? g.getTransform() : null;
            boolean isTransformed = drawImage(g, effect, effects != AwesomeEffect.FOREGROUND, dim, background);
            if (isTransformed && effects == AwesomeEffect.FOREGROUND) {
                g.setTransform(savedState);
            }
            drawBouncingText(g, effect, !isTransformed && effects != AwesomeEffect.BACKGROUND, dim, text, textSize, color, textAlignment);
        }
    }

    public static void drawTextAndIcon(Graphics2D g, AwesomeEffect effect, Dimension textDim, String text, int textSize, Color color, Image icon, Dimension iconDim, int textAlignment) {
        boolean isTransformed = drawImage(g, effect, true, iconDim, icon);
        g.translate(iconDim.width, 0);
        drawBouncingText(g, effect, !isTransformed, textDim, text, textSize, color, textAlignment);
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
                AwesomeEffect effect = user.getEffect();
                effect.setRepeatsLeft(-1);
                user.getEffect().play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                AwesomeEffect effect = user.getEffect();
                effect.setRepeatsLeft(0);
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

    public static void shakeHorizontally(AwesomeEffect.User target, int amount) {
        AwesomeEffect.create()
                .addTranslationXKey(amount, 50)
                .addTranslationXKey(-amount, 150)
                .addTranslationXKey(amount, 250)
                .addTranslationXKey(-amount, 350)
                .addTranslationXKey(0, 400).animate(target);
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

    public static void register(AwesomeEffect.User user, AwesomeEffect effect) {
        if (effect == null) return;
        AwesomeEffect existingEffect = user.getEffect();
        if (existingEffect != null && existingEffect != effect) {
            activeEffects.remove(existingEffect);
            user.setEffect(null);
        }
        if (effect.isAnimated()) {
            activeEffects.add(effect);
        }
    }

    public static void unregister(AwesomeEffect effect) {
        activeEffects.remove(effect);
    }

}
