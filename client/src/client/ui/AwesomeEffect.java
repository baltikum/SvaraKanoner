package client.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/** Used to animate any component that implements the AwesomeEffect.User interface.
 * Example of moving a component in x and y direction by 1000 pixels during 200ms.
 *    AwesomeEffect.create().addTranslationKey(1000, 200).animate(user, AwesomeEffect.FOREGROUND);
 */
public class AwesomeEffect {

    public interface User {
        void setEffect(AwesomeEffect effect);
        AwesomeEffect getEffect();
        Component getComponent();
    }

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
    private final int effects;

    private AwesomeEffect(Key[] keys, User user, int effects) {
        target = user;
        this.keys = keys;
        this.effects = effects;
        durationMillis = keys[keys.length - 1].timeStamp;
    }

    public AwesomeEffect(User user, int x, int y, float scaleX, float scaleY, float degrees) {
        durationMillis = 0;
        effects = 0;
        target = user;
        keys = null;
        this.x = x;
        this.y = y;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.rotation = (float)Math.toRadians(degrees);
    }

    // Play the animation from the beginning in the forward direction.
    public void play() {
        target.setEffect(this);
        elapsedMillis = 0;
        direction = FORWARD;
    }

    // Pauses the animation where it's.
    public void pause() {
        AwesomeUtil.unregister(this);
    }

    // Resumes the animation from where it's.
    public void resume() {
        AwesomeUtil.register(target, this);
    }

    // Resumes the animation from where it's in the opposite direction.
    public void reverse() {
        target.setEffect(this);
        direction = !direction;
    }

    public int getEffects() {
        return effects;
    }

    public boolean isAnimated() {
        return keys != null;
    }

    public Image getSprite() {
        return sprite;
    }

    // Resumes the animation from where it's in the given direction.
    public void setDirection(boolean dir) {
        target.setEffect(this);
        direction = dir;
    }

    public void transform(Graphics2D g, Dimension dimension) {
        float originX = this.originX * dimension.width;
        float originY = this.originY * dimension.height;
        g.setClip(null);
        g.translate(x + originX, y + originY);
        g.rotate(rotation);
        g.scale(scaleX, scaleY);
        g.translate(-originX, -originY);
    }

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

    public static class Builder {
        private final ArrayList<Integer> setFieldFlags = new ArrayList<>();
        private final ArrayList<Key> keys = new ArrayList<>();
        private boolean shouldBounce = false;
        private int numRepeats = 0;
        private float originX = 0.5f, originY = 0.5f;

        public Builder() {
            setKeyFields(TRANSLATION_X | TRANSLATION_Y | SCALE_X | SCALE_Y | ROTATION, 0);
        }

        public Builder repeats(int times) {
            numRepeats = times;
            return this;
        }

        public Builder bounce(boolean val) {
            shouldBounce = val;
            return this;
        }

        public Builder addTranslationKey(int x, int y, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_X | TRANSLATION_Y, timeStamp);
            key.translationX = x;
            key.translationY = y;
            return this;
        }

        public Builder addTranslationXKey(int x, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_X, timeStamp);
            key.translationX = x;
            return this;
        }

        public Builder addTranslationYKey(int y, int timeStamp) {
            Key key = setKeyFields(TRANSLATION_Y, timeStamp);
            key.translationY = y;
            return this;
        }

        public Builder addScaleKey(float x, float y, int timeStamp) {
            Key key = setKeyFields(SCALE_X |SCALE_Y, timeStamp);
            key.scaleX = x;
            key.scaleY = y;
            return this;
        }

        public Builder addScaleXKey(float x, int timeStamp) {
            Key key = setKeyFields(SCALE_X, timeStamp);
            key.scaleX = x;
            return this;
        }

        public Builder addScaleYKey(float y, int timeStamp) {
            Key key = setKeyFields(SCALE_Y, timeStamp);
            key.scaleY = y;
            return this;
        }

        public Builder addRotationKey(float degrees, int timeStamp) {
            Key key = setKeyFields(ROTATION, timeStamp);
            key.rotation = (float)Math.toRadians(degrees);
            return this;
        }

        public Builder addSpriteKey(Image sprite, int timeStamp) {
            Key key = setKeyFields(SPRITES, timeStamp);
            key.sprite = sprite;
            return this;
        }

        // From where the objects scales and rotates, defaults to centrum (0.5, 0.5).
        public void setOrigin(float x, float y) {
            originX = x;
            originY = y;
        }

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

        public void animate(User user, int what) {
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

            AwesomeEffect effect = new AwesomeEffect(keysCopy, user, what);
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
