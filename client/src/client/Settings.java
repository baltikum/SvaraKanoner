package client;

public class Settings {

    public int windowPositionX = -1;
    public int windowPositionY = -1;
    public int windowWidth = 800;
    public int windowHeight = 800;

    public boolean muteMusic = false;
    public boolean muteEffects = false;
    public float musicVolume = 0.8f;
    public float effectsVolume = 0.8f;


    public void validate() {
        musicVolume = Math.max(0.0f, Math.min(1.0f, musicVolume));
        effectsVolume = Math.max(0.0f, Math.min(1.0f, effectsVolume));
    }
}
