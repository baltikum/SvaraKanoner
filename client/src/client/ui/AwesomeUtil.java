package client.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.*;

/**
 * A static utility class that contains support function for the awesome components and effects.
 * And some other things.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class AwesomeUtil {

    public static final int LEFT = 1;
    public static final int CENTER = 2;

    private static float timeSinceStart = 0.0f;
    private static long lastDeltaIncrease = 0;
    private static final Set<AwesomeEffect> activeEffects = new HashSet<>();

    /**
     * Changes the font size of the component to be component.getHeight() * factor upon resize.
     * @param component The components whose font that should be altered.
     * @param factor The fraction of the components height the font size should be.
     */
    public static void dynamicFont(Component component, float factor) {
        component.addComponentListener(new DynamicFont(factor));
        component.dispatchEvent(new ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED));
    }

    /**
     * Draws an animated bouncing text in the given graphics context.
     * @param g The graphics context to render in.
     * @param effect The effect to apply, or null.
     * @param dimension The dimension of the area to render to.
     * @param str The text to be rendered.
     * @param font The font to render with.
     * @param color The color of the text to be rendered.
     * @param alignment The alignment of the text LEFT or CENTER.
     * @return true if the effect was used and the area transformed.
     */
    public static boolean drawBouncingText(Graphics2D g, AwesomeEffect effect, Dimension dimension, String str, Font font, Color color, int alignment) {
        g.setClip(null);
        if (str.isEmpty()) return false;

        boolean isTransformed = false;
        if (effect != null) {
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

    /**
     * Draws a image scaled to fit dim and transformed by the effect if given.
     * @param g The graphics context to render in.
     * @param effect The effect to apply, or null.
     * @param dim The dimension of the area to render to.
     * @param img The image to be rendered, if effect has a sprite for the given key that is used instead.
     * @return true if the effect was used and the area transformed.
     */
    public static boolean drawImage(Graphics2D g, AwesomeEffect effect, Dimension dim, Image img) {
        if (img == null) return false;
        boolean isTransformed = false;
        if (effect != null) {
            effect.transform(g, dim);
            isTransformed = true;
        }
        Image sprite = effect != null ? effect.getSprite() : null;
        g.drawImage(sprite != null ? sprite : img, 0, 0, dim.width, dim.height, null);
        return isTransformed;
    }

    /**
     * Draws an animated bouncing text in the foreground and an image in the background.
     * @param g The graphics context to render in.
     * @param effect The effect to apply, or null.
     * @param dim The dimension of the area to render to.
     * @param text The text to be rendered, effect by FOREGROUND or COMPONENT,
     * @param font The font to render with.
     * @param color The color of the text to be rendered.
     * @param background The background image to render, effect by BACKGROUND or COMPONENT,
     * @param textAlignment The alignment of the text LEFT or CENTER
     */
    public static void drawTextAndBackground(Graphics2D g, AwesomeEffect effect, Dimension dim, String text, Font font, Color color, Image background, int textAlignment) {
        if (effect == null) {
            drawImage(g, null, dim, background);
            drawBouncingText(g, null, dim, text, font, color, textAlignment);
        } else {
            int layer = effect.getEffectedLayer();
            AffineTransform savedState = layer == AwesomeEffect.BACKGROUND ? g.getTransform() : null;
            boolean isTransformed = drawImage(g, layer != AwesomeEffect.FOREGROUND ? effect : null, dim, background);
            if (isTransformed && layer == AwesomeEffect.FOREGROUND) {
                g.setTransform(savedState);
            }
            drawBouncingText(g, !isTransformed && layer != AwesomeEffect.BACKGROUND ? effect : null, dim, text, font, color, textAlignment);
        }
    }

    /**
     * Draws an image to the left and to the right of that animated bouncing text.
     * @param g The graphics context to render in.
     * @param effect The effect to apply, or null. The effect layer of the effect doesn't matter.
     * @param textDim The dimension of the area text area.
     * @param text The text to be rendered.
     * @param font The font to render with.
     * @param color The color of the text to be rendered.
     * @param icon The icon image to render.
     * @param iconDim The size of the icon.
     * @param textAlignment The alignment of the text LEFT or CENTER.
     */
    public static void drawTextAndIcon(Graphics2D g, AwesomeEffect effect, Dimension textDim, String text, Font font, Color color, Image icon, Dimension iconDim, int textAlignment) {
        boolean isTransformed = drawImage(g, effect, iconDim, icon);
        g.translate(iconDim.width, 0);
        drawBouncingText(g, isTransformed ? null : effect, textDim, text, font, color, textAlignment);
    }

    /**
     * Animates the users foregrounds rotation by amount whilst the user is hovering of the component.
     * @param user The component to animate.
     * @param amount The amount of degrees to wiggle by.
     */
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

    /**
     * Scales the components foreground by an amount whilst the mouse is on the component and goes back to 1.0 on exit.
     * @param user The component whose foreground to animate.
     * @param amount The factor to scale by.
     */
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

    /**
     * Quickly shakes the component horizontally. Useful to indicate if something is wrong.
     * @param target The component to shake.
     * @param amount The number of pixels to shake by.
     */
    public static void shakeHorizontally(AwesomeEffect.User target, int amount) {
        AwesomeEffect.create()
                .addTranslationXKey(amount, 50)
                .addTranslationXKey(-amount, 150)
                .addTranslationXKey(amount, 250)
                .addTranslationXKey(-amount, 350)
                .addTranslationXKey(0, 400).animate(target);
    }

    /**
     * Calculates the elapsed time since this was last called and updates all active effects.
     */
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

    /**
     * Should not be called directly use User.setEffect instead. or play, resume on the effect.
     * @param user The components to animate.
     * @param effect The effect to apply to the component.
     */
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

    /**
     * Should not be called directly use AwesomeEffect.pause
     * @param effect
     */
    public static void unregister(AwesomeEffect effect) {
        activeEffects.remove(effect);
    }


    /**
     * Used by dynamicFont.
     */
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
