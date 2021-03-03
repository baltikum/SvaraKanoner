package client;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Handles audio during the game.
 */
public class AudioPlayer {

    public static final String HORN_EFFECT = "https://assets.mixkit.co/sfx/download/mixkit-clown-horn-at-circus-715.wav";
    public static final String CLAPS_EFFECT = "https://assets.mixkit.co/sfx/download/mixkit-small-group-clapping-475.wav";

    private Clip musicClip;
    private final ArrayList<Clip> effectClips = new ArrayList<>();
    private Settings settings;

    /**
     * Constructs an audio playe.
     * The background music plays directly if Settings muteMusic is not true.
     */
    public AudioPlayer() {
        InputStream audioFile = Main.class.getResourceAsStream("/lat1.wav");
        this.settings = Game.game.getSettings();


        playSong(audioFile);

        this.settings.addListener(new Settings.Listener() {
            @Override
            public void propertyChanged(Settings.Properties property, Settings settings) {
                switch (property) {
                    case MUTE_MUSIC -> {
                        if (musicClip != null) {
                            if (settings.isMusicMuted())
                                musicClip.stop();
                            else
                                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                    case MUTE_EFFECTS -> setEffectsVolume(settings.isEffectsMuted() ? 0.0f : settings.getEffectsVolume());
                    case MUSIC_VOLUME -> setVolume(musicClip, settings.getMusicVolume());
                    case EFFECTS_VOLUME -> setEffectsVolume(settings.getEffectsVolume());
                }
            }
        });
    }

    private void playSong(InputStream audioFile) {

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(audioFile))) {

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            musicClip = (Clip) AudioSystem.getLine(info);
            musicClip.open(audioStream);
            setVolume(musicClip, settings.getMusicVolume());

            if (!settings.isMusicMuted())
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
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

    private void stopSong() {
        musicClip.stop();
    }

    public void playEffect(String effect) {
        this.settings = Game.game.getSettings();
        try {
            URL url = new URL(effect);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            setVolume(clip, settings.getEffectsVolume());
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    effectClips.remove(clip);
                }
            });

            setVolume(clip, settings.isEffectsMuted() ? 0.0f : settings.getEffectsVolume());
            clip.start();
            effectClips.add(clip);
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

    private void setEffectsVolume(float volume) {
        for (Clip c : effectClips) {
            setVolume(c, volume);
        }
    }

    private void setVolume(Clip clip, float volume) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }

    public void changeSongAudioPlayer(String song) {
        InputStream audioFile = Main.class.getResourceAsStream("/lat1.wav");;
        stopSong();
        switch (song) {
            case "PickWord" -> audioFile = Main.class.getResourceAsStream("/giss.wav");
            case "Draw" -> audioFile = Main.class.getResourceAsStream("/lat3.wav");
            case "Guess" -> audioFile = Main.class.getResourceAsStream("/giss.wav");
            case "Waiting" -> audioFile = Main.class.getResourceAsStream("/lat2.wav");
            case "Reveal" -> audioFile = Main.class.getResourceAsStream("/lat2.wav");
            case "Winner" -> audioFile = Main.class.getResourceAsStream("/lat2.wav");
        }
        playSong(audioFile);
    }
}
