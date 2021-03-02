package client;

import java.awt.*;
import java.util.ArrayList;

public class Settings {

    public enum Properties {
        MUTE_MUSIC,
        MUTE_EFFECTS,
        MUSIC_VOLUME,
        EFFECTS_VOLUME,
        PREFERRED_AVATAR,
        PREFERRED_NAME
    }

    public interface Listener {
        void propertyChanged(Properties property, Settings settings);
    }

    private int windowPositionX = -1;
    private int windowPositionY = -1;
    private int windowWidth = 800;
    private int windowHeight = 800;

    private boolean muteMusic = true;
    private boolean muteEffects = false;
    private float musicVolume = 0.8f;
    private float effectsVolume = 0.8f;

    private int preferredAvatarId = 0;
    private String preferredName = "Bengt";
    private String ipAddress = "localhost";
    private short socket = 12345;

    private final ArrayList<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }

    private void signalListeners(Properties property) {
        for (Listener l : listeners) {
            l.propertyChanged(property, this);
        }
    }

    public void setWindowBounds(int x, int y, int width, int height) {
        windowPositionX = x;
        windowPositionY = y;
        windowWidth = width;
        windowHeight = height;
    }

    public Rectangle getWindowBounds() {
        return new Rectangle(windowPositionX, windowPositionY, windowWidth, windowHeight);
    }

    public void setMuteMusic(boolean value) {
        muteMusic = value;
        signalListeners(Properties.MUTE_MUSIC);
    }

    public boolean isMusicMuted() {
        return muteMusic;
    }

    public void setMuteEffects(boolean value) {
        muteEffects = value;
        signalListeners(Properties.MUTE_EFFECTS);
    }

    public boolean isEffectsMuted() {
        return muteEffects;
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.min(Math.max(volume, 0.0f), 1.0f);
        signalListeners(Properties.MUSIC_VOLUME);
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setEffectsVolume(float volume) {
        effectsVolume = Math.min(Math.max(volume, 0.0f), 1.0f);
        signalListeners(Properties.EFFECTS_VOLUME);
    }

    public float getEffectsVolume() {
        return  effectsVolume;
    }

    public void nextPreferredIcon() {
        preferredAvatarId -= 1;
        if (preferredAvatarId < 0) preferredAvatarId = 15;
        signalListeners(Properties.PREFERRED_AVATAR);
    }

    public void prevPreferredIcon() {
        preferredAvatarId += 1;
        if (preferredAvatarId > 15) preferredAvatarId = 0;
        signalListeners(Properties.PREFERRED_AVATAR);
    }

    public int getPreferredAvatarId() {
        return preferredAvatarId;
    }

    public void setPreferredName(String name) {
        preferredName = name;
        signalListeners(Properties.PREFERRED_NAME);
    }

    public String getPreferredName() {
        return preferredName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public short getSocket() {
        return socket;
    }

    public void validate() {
        setMusicVolume(musicVolume);
        setEffectsVolume(effectsVolume);
        preferredAvatarId = Math.min(Math.max(preferredAvatarId, 0), 15);
    }
}
