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
            setMusicVolume(settings.getMusicVolume());

            if (!settings.isMusicMuted())
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

        settings.addListener(new Settings.Listener() {
            @Override
            public void propertyChanged(Settings.Properties property, Settings settings) {
                switch (property) {
                    case MUTE_MUSIC -> {
                        if (audioClip != null) {
                            if (settings.isMusicMuted())
                                audioClip.stop();
                            else
                                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                    case MUTE_EFFECTS -> {
                        // TODO: Mute music
                    }
                    case MUSIC_VOLUME -> setMusicVolume(settings.getMusicVolume());
                    case EFFECTS_VOLUME -> {
                        // TODO: Change effects volume
                    }
                }
            }
        });
    }

    private void setMusicVolume(float volume) {
        FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }
}
