package client.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.*;

/** Utility class that contains support function for the awesome components and effects.
 */
public class AwesomeUtil {

    public static final int LEFT = 1;
    public static final int CENTER = 2;

    private static float timeSinceStart = 0.0f;
    private static long lastDeltaIncrease = 0;
    private static final Set<AwesomeEffect> activeEffects = new HashSet<>();

    public static void dynamicFont(Component component, float factor) {
        component.addComponentListener(new DynamicFont(factor));
        component.dispatchEvent(new ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED));
    }

    public static boolean drawBouncingText(Graphics2D g, AwesomeEffect effect, boolean shouldTransform, Dimension dimension, String str, Font font, Color color, int alignment) {
        g.setClip(null);
        if (str.isEmpty()) return false;

        boolean isTransformed = false;
        if (effect != null && shouldTransform) {
            effect.transform(g, dimension);
            isTransformed = true;
        }

        char[] data = new char[str.length()];
        str.getChars(0, str.length(), data, 0);

        g.setColor(color);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();

        int descent = metrics.getDescent();
        int x = 0;
        if (alignment == CENTER) {
            x = dimension.width / 2 - metrics.stringWidth(str) / 2;
        }
        int baseY = dimension.height / 2 + (metrics.getAscent() + descent) / 2 - descent;
        for (int i = 0; i < data.length; i++) {
            int y = baseY - (int)(Math.sin((float)i * .5f + timeSinceStart * 2.0f) * dimension.height * 0.05f);
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

    public static void drawTextAndBackground(Graphics2D g, AwesomeEffect effect, Dimension dim, String text, Font font, Color color, Image background, int textAlignment) {
        if (effect == null) {
            drawImage(g, null, false, dim, background);
            drawBouncingText(g, null, false, dim, text, font, color, textAlignment);
        } else {
            int effects = effect.getEffects();
            AffineTransform savedState = effects == AwesomeEffect.BACKGROUND ? g.getTransform() : null;
            boolean isTransformed = drawImage(g, effect, effects != AwesomeEffect.FOREGROUND, dim, background);
            if (isTransformed && effects == AwesomeEffect.FOREGROUND) {
                g.setTransform(savedState);
            }
            drawBouncingText(g, effect, !isTransformed && effects != AwesomeEffect.BACKGROUND, dim, text, font, color, textAlignment);
        }
    }

    public static void drawTextAndIcon(Graphics2D g, AwesomeEffect effect, Dimension textDim, String text, Font font, Color color, Image icon, Dimension iconDim, int textAlignment) {
        boolean isTransformed = drawImage(g, effect, true, iconDim, icon);
        g.translate(iconDim.width, 0);
        drawBouncingText(g, effect, !isTransformed, textDim, text, font, color, textAlignment);
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


    private static class DynamicFont implements ComponentListener {
        private final float factor;

        public DynamicFont(float factor) {
            this.factor = factor;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            Component target = e.getComponent();
            Font font = target.getFont();
            target.setFont(font.deriveFont(target.getHeight() * factor));
        }

        @Override public void componentMoved(ComponentEvent e) {}
        @Override public void componentShown(ComponentEvent e) {}
        @Override public void componentHidden(ComponentEvent e) {}
    }

}
