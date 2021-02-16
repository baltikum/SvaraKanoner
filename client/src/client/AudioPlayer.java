package client;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private Clip audioClip;

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

    public void muteMusic() {
        Game.game.getSettings().muteMusic = true;
        if (audioClip != null) {
            audioClip.stop();
        }
    }

    public void unmuteMusic() {
        Game.game.getSettings().muteMusic = false;
        if (audioClip != null) {
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void muteEffects() {
        Game.game.getSettings().muteEffects = true;
    }

    public void unmuteEffects() {
        Game.game.getSettings().muteEffects = false;
    }

    public boolean isMusicMuted() {
        return Game.game.getSettings().muteMusic;
    }

    public boolean isEffectsMuted() {
        return Game.game.getSettings().muteEffects;
    }

}
