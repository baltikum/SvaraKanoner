package client;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private boolean isMusicMuted = false;
    private boolean isEffectsMuted = false;
    private Clip audioClip;

    public AudioPlayer() {
        File audioFile = Assets.getResourceFile("bensound-funnysong.wav");

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);

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
        isMusicMuted = true;
        if (audioClip != null) {
            audioClip.stop();
        }
    }

    public void unmuteMusic() {
        isMusicMuted = false;
        if (audioClip != null) {
            audioClip.start();
        }
    }

    public void muteEffects() {
        isEffectsMuted = true;
    }

    public void unmuteEffects() {
        isEffectsMuted = false;
    }

    public boolean isMusicMuted() {
        return isMusicMuted;
    }

    public boolean isEffectsMuted() {
        return isEffectsMuted;
    }

}
