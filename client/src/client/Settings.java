package client;

import common.IniStream;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles one game session from start to finish.
 * Any shared data between the phases should be in here.
 *
 * @author Jesper Jansson
 * @version 03/03/21
 */
public class Settings {

    private static Settings settingsInstance;

    private static String getJarDir() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        int index = path.lastIndexOf('/');
        return path.substring(0, index);
    }

    public static void saveSettings() {
        try {
            System.out.println(getJarDir() + "/settings.ini");
            IniStream.write(settingsInstance, new File(getJarDir() + "/settings.ini"));
        } catch (IOException ignored) {}
    }

    public static Settings getSettings() {
        if (settingsInstance == null) {
            settingsInstance = new Settings();
            try {
                IniStream.read(settingsInstance, new File(getJarDir() + "/settings.ini"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            settingsInstance.validate();
        }
        return settingsInstance;
    }

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

    private Settings() {}

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

    /**
     * Used to set preferred host IP address.
     * @param ip the ip.
     */
    public void setIpAddress(String ip ) {
        if (validateIP(ip)) {
            this.ipAddress = ip;
        }
    }

    /**
     * Helper to check if IP is valid.
     * @param ip
     * @return
     */
    private boolean validateIP(String ip ) {
        String[] temp = ip.split("[.]");

        if ( !(temp.length == 4) ) {
            return false;
        }

        int[] temp2 = {
                Integer.parseInt(temp[0]),
                Integer.parseInt(temp[1]),
                Integer.parseInt(temp[2]),
                Integer.parseInt(temp[3])
        };

        for ( int i = 0; i<4; i++ ) {
            if ( ((temp2[i] - 255) > 0) || (temp2[i] < 0) ) {
                return false;
            }
        }
        return true;
    }

    public short getSocket() {
        return socket;
    }

    private void validate() {
        setMusicVolume(musicVolume);
        setEffectsVolume(effectsVolume);
        preferredAvatarId = Math.min(Math.max(preferredAvatarId, 0), 15);
    }
}
