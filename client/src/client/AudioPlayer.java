package client;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Handles audio during the game.
 */
public class AudioPlayer {

    private Clip audioClip;

    /**
     * Constructs an audio playe.
     * The background music plays directly if Settings muteMusic is not true.
     */
    public AudioPlayer() {
        File audioFile = Assets.getResourceFile("bensound-funnysong.wav");
        Settings settings = Game.game.getSettings();
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);

            if (!settings.muteMusic)
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);

            FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * settings.musicVolume) + gainControl.getMinimum();
            gainControl.setValue(gain);

        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }

    }

    /**
     * Changes the settings muteMusic to true and stops the music.
     */
    public void muteMusic() {
        Game.game.getSettings().muteMusic = true;
        if (audioClip != null) {
            audioClip.stop();
        }
    }


    /**
     * Changes the settings muteMusic to false and resumes the music.
     */
    public void unmuteMusic() {
        Game.game.getSettings().muteMusic = false;
        if (audioClip != null) {
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * For now only changes the muteEffects to true setting.
     */
    public void muteEffects() {
        Game.game.getSettings().muteEffects = true;
    }

    /**
     * For now only changes the muteEffects to false setting.
     */
    public void unmuteEffects() {
        Game.game.getSettings().muteEffects = false;
    }

    /**
     * @return true If settings muteMusic is true else false.
     */
    public boolean isMusicMuted() {
        return Game.game.getSettings().muteMusic;
    }

    /**
     * @return true If settings muteEffects is true else false.
     */
    public boolean isEffectsMuted() {
        return Game.game.getSettings().muteEffects;
    }

}
