package client;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


/**
 * Handles all audio inside the game.
 *
 * This is a singleton and can be retrieved from getInstance.
 *
 * @author Jesper Jansson
 * @version 03/03/21
 */
public class AudioPlayer {

    private static AudioPlayer instance;

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public static final String HORN_EFFECT = "https://assets.mixkit.co/sfx/download/mixkit-clown-horn-at-circus-715.wav";
    public static final String CLAPS_EFFECT = "https://assets.mixkit.co/sfx/download/mixkit-small-group-clapping-475.wav";

    private Clip musicClip;
    private final ArrayList<Clip> effectClips = new ArrayList<>();
    private final Settings settings;

    /**
     * Constructs an audio playe.
     * The background music plays directly if Settings muteMusic is not true.
     */
    private AudioPlayer() {
        settings = Settings.getSettings();
        settings.addListener((property, settings) -> {
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
        });
    }

    private void playSong(InputStream audioFile) {
        if (musicClip != null) {
            musicClip.stop();
        }
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

    /**
     * Play a given effect.
     * @param effect A path to the effect to play.
     */
    public void playEffect(String effect) {
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

    /**
     *
     * @param song Can be PickWord, Draw, Guess, Waiting, Reveal, Winner
     */
    public void changeSongAudioPlayer(String song) {
        InputStream audioFile = Main.class.getResourceAsStream("/lat1.wav");;
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
}
