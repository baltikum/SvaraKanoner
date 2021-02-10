package client.ui;

import java.awt.*;
import java.util.ArrayList;

public class AwesomeEffect {

    public interface User {
        void setEffect(AwesomeEffect effect);
    }

    private static final byte ROTATION = 1;
    private static final byte TRANSLATION_X = 2;
    private static final byte TRANSLATION_Y = 4;
    private static final byte SCALE_X = 8;
    private static final byte SCALE_Y = 16;
    private static final byte SPRITES = 32;

    public static Builder create() {
        return new Builder();
    }

    private Rectangle sprite;
    private int[] floatFrameIndices;
    private FloatTimeline[] floatTimelines;
    private SpriteTimeline spriteTimeline;
    private int elapsedTime = 0;

    private float x, y, scaleX = 1.0f, scaleY = 1.0f, rotation;

    private AwesomeEffect(FloatTimeline[] floatTimelines,
                          SpriteTimeline spriteTimeline) {
        if (floatTimelines != null)
            floatFrameIndices = new int[floatTimelines.length];
        this.floatTimelines = floatTimelines;
        this.spriteTimeline = spriteTimeline;
    }

    public void transform(Graphics2D g) {
        g.setClip(null);
        g.scale(scaleX, scaleY);
        g.rotate(rotation);
        g.translate((int)x, (int)y);
    }

    public void update(int deltaMillis) {
        elapsedTime += deltaMillis;
        if (floatTimelines != null) {
            int timelineIndex = 0;
            for (; timelineIndex < floatTimelines.length; timelineIndex++) {
                FloatTimeline timeline = floatTimelines[timelineIndex];
                if (timeline == null) break;
                int frameIndex = floatFrameIndices[timelineIndex];

                float lastFrameTarget = frameIndex > 0 ? timeline.targetValues[frameIndex - 1] : 0.0f;
                int lastFrameEndTime = frameIndex > 0 ? timeline.timeStamps[frameIndex - 1] : 0;

                int duration = timeline.timeStamps[frameIndex] - lastFrameEndTime;
                int elapsedThisFrame = elapsedTime - lastFrameEndTime;
                float delta = (float)elapsedThisFrame / (float)duration;
                float value = lastFrameTarget + (timeline.targetValues[frameIndex] - lastFrameTarget) * delta;
                switch (timeline.targetField) {
                    case (TRANSLATION_X) -> x = value;
                    case (TRANSLATION_Y) -> y = value;
                    case (SCALE_X) -> scaleX = value;
                    case (SCALE_Y) -> scaleY = value;
                    case (ROTATION) -> rotation = value;
                }
            }
        }
    }

    // =====================================================================
    // Inner classes below
    // =====================================================================

    private static class FloatTimeline {
        public int totalDuration;
        public byte targetField;
        public int[] timeStamps;
        public float[] targetValues; // length = timeStamps.length

        FloatTimeline(byte targetField, float[] targetValues, int[] timeStamps, int totalDuration) {
            this.targetField = targetField;
            this.timeStamps = timeStamps;
            this.targetValues = targetValues;
            this.totalDuration = totalDuration;
        }
    }

    private static class SpriteTimeline {
        public int[] timeStamps;
        public int[] targets; // length = timeStamps.length * 4

        SpriteTimeline(int[] targets, int[] timeStamps) {
            this.timeStamps = timeStamps;
            this.targets = targets;
        }
    }

    public static class Builder {
        private final ArrayList<Integer> floatDurations = new ArrayList<>();
        private final ArrayList<Float> floatValues = new ArrayList<>();
        private final ArrayList<Byte> floatTargets = new ArrayList<>();

        private final ArrayList<Integer> spriteDurations = new ArrayList<>();
        private final ArrayList<Rectangle> spriteTargets = new ArrayList<>();
        private byte effectedTimelines = 0;

        private Builder addFloat(float value, byte target, int duration) {
            effectedTimelines |= target;
            floatValues.add(value);
            floatTargets.add(target);
            floatDurations.add(duration);
            return this;
        }
        public Builder animateX(float amount, int duration) {
            return addFloat(amount, TRANSLATION_X, duration);
        }

        public Builder animateY(float amount, int duration) {
            return addFloat(amount, TRANSLATION_Y, duration);
        }

        public Builder animatePosition(float x, float y, int duration) {
            addFloat(x, TRANSLATION_X, duration);
            return addFloat(y, TRANSLATION_Y, duration);
        }

        public Builder animateScaleX(float amount, int duration) {
            return addFloat(amount, SCALE_X, duration);
        }

        public Builder animateScaleY(float amount, int duration) {
            return addFloat(amount, SCALE_Y, duration);
        }

        public Builder animateScale(float x, float y, int duration) {
            addFloat(x, SCALE_X, duration);
            return addFloat(y, SCALE_Y, duration);
        }

        public Builder animateRotation(float amount, int duration) {
            return addFloat(amount, ROTATION, duration);
        }

        public Builder animateSprite(int x, int y, int width, int height, int duration) {
            effectedTimelines |= SPRITES;
            spriteTargets.add(new Rectangle(x, y, width, height));
            spriteDurations.add(duration);
            return this;
        }

        private FloatTimeline createTimelineFor(byte target) {
            if ((effectedTimelines & target) == 0) return null;
            int length = 0;
            for (Byte floatTarget : floatTargets) {
                if (floatTarget == TRANSLATION_X) ++length;
            }
            int timeStamp = 0;
            int[] durations = new int[length];
            float[] values = new float[length];
            int targetIndex = 0;
            for (int index = 0; index < floatTargets.size(); index++) {
                if (floatTargets.get(index) == TRANSLATION_X) {
                    timeStamp += floatDurations.get(index);
                    values[targetIndex] = floatValues.get(index);
                    durations[targetIndex] = timeStamp;
                    ++targetIndex;
                }
            }
            return new FloatTimeline(target, values, durations, timeStamp);
        }

        private SpriteTimeline createSpriteTimeline() {
            if (spriteTargets.isEmpty()) return null;
            int timeStamp = 0;
            int[] timeStamps = new int[spriteDurations.size()];
            int[] targets = new int[timeStamps.length * 4];
            for (int index = 0; index < timeStamps.length; index++) {
                timeStamp += spriteDurations.get(index);
                timeStamps[index] = timeStamp;
                Rectangle rect = spriteTargets.get(index);
                targets[index * 4]     = rect.x;
                targets[index * 4 + 1] = rect.y;
                targets[index * 4 + 2] = rect.width;
                targets[index * 4 + 3] = rect.height;
            }
            return new SpriteTimeline(targets, timeStamps);
        }

        public void animate(User user) {
            int numFloatTimelines = 0;
            int index = 0;
            if ((effectedTimelines & TRANSLATION_X) != 0) ++numFloatTimelines;
            if ((effectedTimelines & TRANSLATION_Y) != 0) ++numFloatTimelines;
            if ((effectedTimelines & SCALE_X) != 0) ++numFloatTimelines;
            if ((effectedTimelines & SCALE_Y) != 0) ++numFloatTimelines;
            if ((effectedTimelines & ROTATION) != 0) ++numFloatTimelines;
            FloatTimeline[] floatTimelines = new FloatTimeline[numFloatTimelines];
            if ((effectedTimelines & TRANSLATION_X) != 0) floatTimelines[index++] = createTimelineFor(TRANSLATION_X);
            if ((effectedTimelines & TRANSLATION_Y) != 0) floatTimelines[index++] = createTimelineFor(TRANSLATION_X);
            if ((effectedTimelines & SCALE_X) != 0) floatTimelines[index++] = createTimelineFor(TRANSLATION_X);
            if ((effectedTimelines & SCALE_Y) != 0) floatTimelines[index++] = createTimelineFor(TRANSLATION_X);
            if ((effectedTimelines & ROTATION) != 0) floatTimelines[index] = createTimelineFor(TRANSLATION_X);

            AwesomeEffect effect = new AwesomeEffect(floatTimelines, createSpriteTimeline());
            user.setEffect(effect);
            AwesomeUtil.register(effect);
        }
    }
}
