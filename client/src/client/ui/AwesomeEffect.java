package client.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Can be used to translate the apperance of any component extending the AwesomeEffect.User interface.
 * It can translate, scale and rotate the apperance as well as changing the image the component.
 *
 * @author Jesper Jansson
 * @version 17/02/21
 */
public class AwesomeEffect {

    /**
     * Implement this to allow your component to be animated.
     */
    public interface User {
        /**
         * Should store the effect in the component and call AwesomeUtil.register
         * @param effect The effect to set.
         */
        void setEffect(AwesomeEffect effect);

        /**
         * Should return the effect last set by set Effect.
         * @return The set effect or null if non has been set.
         */
        AwesomeEffect getEffect();

        /**
         * Should return this of any component implementing this interface.
         * @return Return the implementer of the interface.
         */
        Component getComponent();
    }

    public static final int INFINITY = -1;
    public static final boolean FORWARD = false;
    public static final boolean BACKWARDS = true;

    public static final int COMPONENT = 1;
    public static final int FOREGROUND = 2;
    public static final int BACKGROUND = 3;

    private static final int ROTATION = 1;
    private static final int TRANSLATION_X = 2;
    private static final int TRANSLATION_Y = 4;
    private static final int SCALE_X = 8;
    private static final int SCALE_Y = 16;
    private static final int SPRITES = 32;

    /**
     * Creates a builder to start creating the effect.
     * @return A instance of AwesomeEffect.Builder
     */
    public static Builder create() {
        return new Builder();
    }

    private final User target;
    private final Key[] keys;
    private Image sprite;

    private float originX, originY;
    private int x, y;
    private float scaleX = 1.0f, scaleY = 1.0f, rotation;

    private boolean direction;
    private boolean shouldBounce;
    private final int durationMillis;
    private int elapsedMillis;
    private int repeatsLeft;
    private final int effectedLayer;

    private AwesomeEffect(Key[] keys, User user, int effectedLayer) {
        target = user;
        this.keys = keys;
        this.effectedLayer = effectedLayer;
        durationMillis = keys[keys.length - 1].timeStamp;
    }

    /**
     * Used to create a static effect with no animation.
     * @param user The user to apply the effect to.
     * @param x The amount to translate the component horizontally.
     * @param y The amount to translate the component vertically.
     * @param scaleX The amount to scale the components width by, 1.0 is no scaling.
     * @param scaleY The amount to scale the components height by, 1.0 is no scaling.
     * @param degrees The degrees to rotate the component by.
     */
    public AwesomeEffect(User user, int x, int y, float scaleX, float scaleY, float degrees, int effectedLayer) {
        durationMillis = 0;
        this.effectedLayer = effectedLayer;
        target = user;
        keys = null;
        this.x = x;
        this.y = y;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = (float)Math.toRadians(degrees);
    }

    /**
     * Plays the animation from the beginning in the forwards direction,
     * if already playing restarts the animation from the beginning.
     */
    public void play() {
        target.setEffect(this);
        elapsedMillis = 0;
        direction = FORWARD;
    }

    /**
     * Stops the animation and remembers it's position.
     */
    public void pause() {
        AwesomeUtil.unregister(this);
    }

    /**
     * Resumes the animation from where it was last, if already playing it does nothing.
     */
    public void resume() {
        AwesomeUtil.register(target, this);
    }

    /**
     * Changes the direction of the animation, if the animation is paused it also resumes it.
     */
    public void reverse() {
        AwesomeUtil.register(target, this);
        direction = !direction;
    }
    /**
     * Sets the direction of the animation, if the animation is paused it also resumes it.
     * @param dir The direction to go in.
     */
    public void setDirection(boolean dir) {
        AwesomeUtil.register(target, this);
        direction = dir;
    }

    /**
     * Sets the number of repeats left, 0 means it will stop when it reaches the end. INFINITY means it will never stop.
     * @param repeats
     */
    public void setRepeatsLeft(int repeats) {
        repeatsLeft = repeats;
    }

    /**
     * Returns the effected layer, either FOREGROUND, BACKGROUND or COMPONENT.
     * @return The effected layer.
     */
    public int getEffectedLayer() {
        return effectedLayer;
    }

    /**
     * Returns true if it's no a static effect.
     * @return True if changes over time.
     */
    public boolean isAnimated() {
        return keys != null;
    }

    /**
     * Returns the sprite for the current key frame.
     * @return The image that should be rendered for this frame.
     */
    public Image getSprite() {
        return sprite;
    }

    /**
     * Transforms the given graphics context to represent the current time in the animation.
     * @param g The context to transform.
     * @param dimension The size of the area to transform, need to react appropriatly to the effects origin.
     */
    public void transform(Graphics2D g, Dimension dimension, boolean shouldClip) {
        float originX = this.originX * dimension.width;
        float originY = this.originY * dimension.height;
        if (!shouldClip) g.setClip(null);
        g.translate(x + originX, y + originY);
        g.rotate(rotation);
        g.scale(scaleX, scaleY);
        g.translate(-originX, -originY);
    }

    /**
     * Increases the elapsed time by a given amount and updates the state of the effect.
     * @param deltaMillis The amount to forward.
     * @return Returns true if it reached the end and there is no more repeats, else false.
     */
    public boolean update(int deltaMillis) {
        elapsedMillis += direction == FORWARD ? deltaMillis : -deltaMillis;
        boolean reachedEnd = false;
        Key key0, key1 = null;
        if (elapsedMillis < 0 || elapsedMillis > durationMillis) {
            key0 = direction == FORWARD ? keys[keys.length - 1] : keys[0];
            if (repeatsLeft == 0) {
                reachedEnd = true;
                elapsedMillis = direction == FORWARD ? durationMillis : 0;
            } else {
                if (repeatsLeft > 0) --repeatsLeft;
                if (shouldBounce) {
                    elapsedMillis = direction == FORWARD ? durationMillis : 0;
                    direction = !direction;
                } else {
                    elapsedMillis = direction == FORWARD ? 0 : durationMillis;
                }
            }
        } else {
            key0 = keys[0];
            if (keys.length > 1) {
                int index = 1;
                key1 = keys[1];
                while (elapsedMillis > key1.timeStamp) {
                    key0 = key1;
                    key1 = keys[++index];
                }
            }
        }

        if (key1 == null) {
            x = key0.translationX;
            y = key0.translationY;
            scaleX = key0.scaleX;
            scaleY = key0.scaleY;
            rotation = key0.rotation;
        } else {
            float delta = (float)(elapsedMillis - key0.timeStamp) / (float)(key1.timeStamp - key0.timeStamp);
            x = key0.translationX + (int)((key1.translationX - key0.translationX) * delta);
            y = key0.translationY + (int)((key1.translationY - key0.translationY) * delta);
            scaleX = key0.scaleX + (key1.scaleX - key0.scaleX) * delta;
            scaleY = key0.scaleY + (key1.scaleY - key0.scaleY) * delta;
            rotation = key0.rotation + (key1.rotation - key0.rotation) * delta;
        }
        sprite = key0.sprite;

        return reachedEnd;
    }

    // =====================================================================
    // Helper classes below
    // =====================================================================

    /**
     * A data class to represent a given time in the animation.
     */
    private static class Key implements Cloneable {
        private final int timeStamp;
        private float scaleX = 1.0f, scaleY = 1.0f, rotation;
        private int translationX, translationY;
        private Image sprite;

        Key(int timeStamp) {
            this.timeStamp = timeStamp;
        }

        @Override
        public Key clone() {
            try {
                return (Key) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Helps in the construction of an AwesomeEffect.
     */
    public static class Builder {
        private final ArrayList<Integer> setFieldFlags = new ArrayList<>();
        private final ArrayList<Key> keys = new ArrayList<>();
        private boolean shouldBounce = false;
        private int numRepeats = 0;
        private float originX = 0.5f, originY = 0.5f;

        public Builder() {
            setKeyFields(TRANSLATION_X | TRANSLATION_Y | SCALE_X | SCALE_Y | ROTATION, 0);
        }

        /**
         * Set the number of repeats of the effect. Decreases each time the player bounces or restarts.
         * @param times Number of times to repeat.
         * @return this
         */
        public Builder repeats(int times) {
            numRepeats = times;
            return this;
        }

        /**
         *
         * @param val If true the animation will bounce back and forth, if
         *            false the animation will restart and continue in the same direction.
         * @return this
         */
        public Builder bounce(boolean val) {
            shouldBounce = val;
            return this;
        }

        /**
         * Adds a key to move the targets position to.
         * @param x Amount to translate horizontally.
         * @param y Amount to translate vertically.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addTranslationKey(int x, int y, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_X | TRANSLATION_Y, timeStamp);
            key.translationX = x;
            key.translationY = y;
            return this;
        }

        /**
         * Adds a key to move the targets horizontal position to.
         * @param x Amount to translate horizontally.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addTranslationXKey(int x, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_X, timeStamp);
            key.translationX = x;
            return this;
        }

        /**
         * Adds a key to move the targets vertical position to.
         * @param y Amount to translate vertically.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addTranslationYKey(int y, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_Y, timeStamp);
            key.translationY = y;
            return this;
        }

        /**
         * Adds a key to scale the targets size by.
         * @param x Amount to scale width by.
         * @param y Amount to scale height by.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addScaleKey(float x, float y, int timeStamp) {
            Key key = setKeyFields(SCALE_X |SCALE_Y, timeStamp);
            key.scaleX = x;
            key.scaleY = y;
            return this;
        }

        /**
         * Adds a key to scale the targets width by.
         * @param x Amount to scale width by.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addScaleXKey(float x, int timeStamp) {
            Key key = setKeyFields(SCALE_X, timeStamp);
            key.scaleX = x;
            return this;
        }

        /**
         * Adds a key to scale the targets height by.
         * @param y Amount to scale height by.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addScaleYKey(float y, int timeStamp) {
            Key key = setKeyFields(SCALE_Y, timeStamp);
            key.scaleY = y;
            return this;
        }

        /**
         * Adds a key to rotate the target by.
         * @param degrees Amount to rotate the target by.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addRotationKey(float degrees, int timeStamp) {
            Key key = setKeyFields(ROTATION, timeStamp);
            key.rotation = (float)Math.toRadians(degrees);
            return this;
        }

        /**
         * Sets the current sprite of the given timeStamp and this sprite will be held until the next key where it's set.
         * @param sprite The image to be rendered at this key and after.
         * @param timeStamp The absolute time of the position.
         * @return this
         */
        public Builder addSpriteKey(Image sprite, int timeStamp) {
            Key key = setKeyFields(SPRITES, timeStamp);
            key.sprite = sprite;
            return this;
        }

        /**
         * Sets the origin to scale and rotate around, defaults to (0.5, 0.5) which represents the center.
         * @param x The origin horizontally, 0 is the left edge 1 is the right.
         * @param y The origin vertically, 0 is the top edge 1 is the bottom.
         */
        public void setOrigin(float x, float y) {
            originX = x;
            originY = y;
        }

        /**
         * Returns an existing key for the given time stamp or create a new one.
         * And marks what fields are set given by fields.
         * @param fields The fields that are will be set in the key.
         * @param timeStamp At what time the key exists.
         * @return The key for the given timeStamp.
         */
        private Key setKeyFields(int fields, int timeStamp) {
            Key key = null;
            int keyIndex = 0;
            for (; keyIndex < keys.size(); keyIndex++) {
                Key it = keys.get(keyIndex);
                if (it.timeStamp >= timeStamp) {
                    if (it.timeStamp == timeStamp) {
                        key = it;
                        setFieldFlags.set(keyIndex, setFieldFlags.get(keyIndex) | fields);
                    }
                    break;
                }
            }
            if (key == null) {
                key = new Key(timeStamp);
                keys.add(keyIndex, key);
                setFieldFlags.add(keyIndex, fields);
            }
            return key;
        }

        /**
         * Creates an instance of AwesomeEffect with all properties set until this point.
         * @param user The target to effect.
         * @param effectedLayer What layer to effect FOREGROUND, BACKGROUND or COMPONENT.
         */
        public void animate(User user, int effectedLayer) {
            if (user.getEffect() != null) {
                user.setEffect(null);
            }

            int transXLastSetIndex = 0;
            int transYLastSetIndex = 0;
            int scaleXLastSetIndex = 0;
            int scaleYLastSetIndex = 0;
            int rotLastSetIndex = 0;

            Key prevKey = null;
            Key[] keysCopy = new Key[keys.size()];
            for (int i = 0; i < keysCopy.length; i++) {
                Key keyCopy = keys.get(i).clone();
                if (prevKey != null) {
                    int setFields = setFieldFlags.get(i);

                    if ((setFields & TRANSLATION_X) != 0) {
                        if (transXLastSetIndex < (i - 1)) {
                            int valueStart = keysCopy[transXLastSetIndex].translationX;
                            int valueDiff = keyCopy.translationX - valueStart;
                            int timeStart = keysCopy[transXLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = transXLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].translationX = (int)(valueStart + valueDiff * delta);
                            }
                        }
                        transXLastSetIndex = i;
                    } else {
                        keyCopy.translationX = prevKey.translationX;
                    }

                    if ((setFields & TRANSLATION_Y) != 0) {
                        if (transYLastSetIndex < (i - 1)) {
                            int valueStart = keysCopy[transYLastSetIndex].translationY;
                            int valueDiff = keyCopy.translationY - valueStart;
                            int timeStart = keysCopy[transYLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = transYLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].translationY = (int)(valueStart + valueDiff * delta);
                            }
                        }
                        transYLastSetIndex = i;
                    } else {
                        keyCopy.translationY = prevKey.translationY;
                    }

                    if ((setFields & SCALE_X) != 0) {
                        if (scaleXLastSetIndex < (i - 1)) {
                            float valueStart = keysCopy[scaleXLastSetIndex].scaleX;
                            float valueDiff = keyCopy.scaleX - valueStart;
                            int timeStart = keysCopy[scaleXLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = scaleXLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].scaleX = valueStart + valueDiff * delta;
                            }
                        }
                        scaleXLastSetIndex = i;
                    } else {
                        keyCopy.scaleX = prevKey.scaleX;
                    }

                    if ((setFields & SCALE_Y) != 0) {
                        if (scaleYLastSetIndex < (i - 1)) {
                            float valueStart = keysCopy[scaleYLastSetIndex].scaleY;
                            float valueDiff = keyCopy.scaleY - valueStart;
                            int timeStart = keysCopy[scaleYLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = scaleYLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].scaleY = (int)(valueStart + valueDiff * delta);
                            }
                        }
                        scaleYLastSetIndex = i;
                    } else {
                        keyCopy.scaleY = prevKey.scaleY;
                    }

                    if ((setFields & TRANSLATION_Y) != 0) {
                        if (transYLastSetIndex < (i - 1)) {
                            int valueStart = keysCopy[transYLastSetIndex].translationY;
                            int valueDiff = keyCopy.translationY - valueStart;
                            int timeStart = keysCopy[transYLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = transYLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].translationY = (int)(valueStart + valueDiff * delta);
                            }
                        }
                        transYLastSetIndex = i;
                    } else {
                        keyCopy.translationY = prevKey.translationY;
                    }

                    if ((setFields & ROTATION) != 0) {
                        if (rotLastSetIndex < (i - 1)) {
                            float valueStart = keysCopy[rotLastSetIndex].rotation;
                            float valueDiff = keyCopy.rotation - valueStart;
                            int timeStart = keysCopy[rotLastSetIndex].timeStamp;
                            float duration = keyCopy.timeStamp - timeStart;
                            for (int i1 = rotLastSetIndex + 1; i1 < i; i1++) {
                                float delta = (float)(keysCopy[i1].timeStamp - timeStart) / duration;
                                keysCopy[i1].rotation = (int)(valueStart + valueDiff * delta);
                            }
                        }
                        rotLastSetIndex = i;
                    } else {
                        keyCopy.rotation = prevKey.rotation;
                    }

                    if ((setFields & SPRITES) == 0)  keyCopy.sprite = prevKey.sprite;
                }
                keysCopy[i] = keyCopy;
                prevKey = keyCopy;
            }

            AwesomeEffect effect = new AwesomeEffect(keysCopy, user, effectedLayer);
            effect.originX = originX;
            effect.originY = originY;
            effect.shouldBounce = shouldBounce;
            effect.repeatsLeft = numRepeats;
            user.setEffect(effect);
        }

        public void animate(User user) {
            animate(user, COMPONENT);
        }
    }
}
