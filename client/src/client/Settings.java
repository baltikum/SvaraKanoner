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

    /**
     * Saves the settings in its current state to settings.ini next to the jar file.
     */
    public static void saveSettings() {
        try {
            System.out.println(getJarDir() + "/settings.ini");
            IniStream.write(settingsInstance, new File(getJarDir() + "/settings.ini"));
        } catch (IOException ignored) {}
    }

    /**
     * Retrieves the settings, the first time this is called it is loaded from the settings.ini file.
     * @return The settings.
     */
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
        /**
         * Fired when a property in the settings instance changes.
         * @param property The property that changed.
         * @param settings The settings instance.
         */
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

    /**
     * Adds a listener to be notified upon changing of the settings properties.
     * @param listener The listener that should be notified.
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removed a previously added listener from getting more notifications.
     * @param listener The listener to remove.
     * @return True if it was previously added, else false.
     */
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }

    /**
     * Notify all added listeners to a specific property change.
     * @param property The property that changed.
     */
    private void signalListeners(Properties property) {
        for (Listener l : listeners) {
            l.propertyChanged(property, this);
        }
    }

    /**
     * Set the window position and size settings for positioning the window upon start.
     * @param x X position in pixels.
     * @param y Y position in pixels.
     * @param width Width in pixels.
     * @param height Height in pixels.
     */
    public void setWindowBounds(int x, int y, int width, int height) {
        windowPositionX = x;
        windowPositionY = y;
        windowWidth = width;
        windowHeight = height;
    }

    /**
     * window position and size else negative values.
     * @return The last saved
     */
    public Rectangle getWindowBounds() {
        return new Rectangle(windowPositionX, windowPositionY, windowWidth, windowHeight);
    }

    /**
     * Sets if music should be played or not.
     * @param value Plays if false.
     */
    public void setMuteMusic(boolean value) {
        muteMusic = value;
        signalListeners(Properties.MUTE_MUSIC);
    }

    /**
     *
     * @return True if the music should be muted.
     */
    public boolean isMusicMuted() {
        return muteMusic;
    }

    /**
     * Sets if effects should be played or not.
     * @param value Plays if false.
     */
    public void setMuteEffects(boolean value) {
        muteEffects = value;
        signalListeners(Properties.MUTE_EFFECTS);
    }

    /**
     *
     * @return True if the effects should be muted.
     */
    public boolean isEffectsMuted() {
        return muteEffects;
    }

    /**
     * Set the volume of the music.
     * @param volume  0 is of 1 is max.
     */
    public void setMusicVolume(float volume) {
        musicVolume = Math.min(Math.max(volume, 0.0f), 1.0f);
        signalListeners(Properties.MUSIC_VOLUME);
    }

    /**
     * @return The music volume between 0 and 1.
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Set the volume of the music.
     * @param volume  0 is of 1 is max.
     */
    public void setEffectsVolume(float volume) {
        effectsVolume = Math.min(Math.max(volume, 0.0f), 1.0f);
        signalListeners(Properties.EFFECTS_VOLUME);
    }

    /**
     * @return The effecs volume between 0 and 1.
     */
    public float getEffectsVolume() {
        return  effectsVolume;
    }

    /**
     * Cycles the avatar icons to the next icon.
     */
    public void nextPreferredIcon() {
        preferredAvatarId -= 1;
        if (preferredAvatarId < 0) preferredAvatarId = 15;
        signalListeners(Properties.PREFERRED_AVATAR);
    }

    /**
     * Cycles the avatar icons to the next icon in the other direction then nextPreferredIcon.
     */
    public void prevPreferredIcon() {
        preferredAvatarId += 1;
        if (preferredAvatarId > 15) preferredAvatarId = 0;
        signalListeners(Properties.PREFERRED_AVATAR);
    }

    /**
     * Returns the preferred avatar id.
     * @return A integer in the range [0, 16).
     */
    public int getPreferredAvatarId() {
        return preferredAvatarId;
    }

    /**
     * Sets the preferred name for this client.
     * @param name The name.
     */
    public void setPreferredName(String name) {
        preferredName = name;
        signalListeners(Properties.PREFERRED_NAME);
    }

    /**
     * Gets the preferred name for this client.
     * @return The name....
     */
    public String getPreferredName() {
        return preferredName;
    }

    /**
     * @return Get the ip address that the tcp connection should target.
     */
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
     * @param ip The ip address to check
     * @return True if it's valid else false.
     */
    private boolean validateIP(String ip) {
        String[] temp = ip.split("[.]");

        if ( !(temp.length == 4) ) {
            return false;
        }

        try {
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
        } catch (NumberFormatException ignore) {
            return false;
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
