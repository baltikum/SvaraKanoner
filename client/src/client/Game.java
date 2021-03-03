package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;


public class Game implements ActionListener, WindowListener {
    public static Game game;

    public final Random random = new Random();
    private final JFrame frame;
    private Phase currentPhase;

    private final Settings settings = new Settings();
    public Chat chat;
    private final AudioPlayer audioPlayer;

    private Network network;
    private JLabel errorMsg;

    private GameSettings gameSettings = new GameSettings();
    private String gameCode = "---";
    private final Player thisPlayer = new Player(-1, "Bengt", 0);
    private final List<Player> players = new ArrayList<>();

    private final PhaseUI phaseUI = new PhaseUI();

    Game() {
        game = this;

        try {
            IniStream.read(settings, new File(getJarDir() + "/settings.ini"));
            settings.validate();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        audioPlayer = new AudioPlayer();


        // Initiate the window
        Rectangle windowBounds = settings.getWindowBounds();
        frame = new JFrame("Ryktet går!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setBackground(new Color(0xe67e22));
        frame.setPreferredSize(new Dimension(windowBounds.width, windowBounds.height));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setPreferredSize(new Dimension(800, 800));

        // Initiate the LayeredPane
        initTopLayer();

        // Start the network
        network = new Network(settings);
        network.start();

        // Start with
        //setCurrentPhase(new MainMenu());
        // Message msg = new Message(Message.Type.PICK_WORD);
        // msg.addParameter("words", new String[]{"aaa", "aaa", "asd", "aasd"});
        // setCurrentPhase(new PickWordPhase(msg));
        // setCurrentPhase(new WinnerPhase(new Message(Message.Type.GOTO)));
         setCurrentPhase(new DrawPhase(new Message(Message.Type.GOTO)));
        // setCurrentPhase(new WaitingPhase(new Message(Message.Type.GOTO)));
        // setCurrentPhase(new GuessPhase(new Message(Message.Type.GOTO)));

        // Move and show window
        if (windowBounds.x < 0 || windowBounds.y < 0)
            frame.setLocationRelativeTo(null);
        else
            frame.setLocation(windowBounds.x, windowBounds.y);
        frame.setVisible(true);

        // Start timer for updating graphics
        Timer timer = new Timer(1000 / 30, this);
        timer.setInitialDelay(1000 / 30);
        timer.start();

    }

    private void initTopLayer() {
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout) {
            @Override
            public void paintComponent(Graphics g) {}
        };
        // panel.setPreferredSize(new Dimension(500, 500));
        frame.setGlassPane(panel);
        panel.setVisible(true);
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));

        // Icons
        BufferedImage icons = Assets.loadImage("ui-icons.png");
        Image muteMusicIcon = Assets.getTile(icons, 0, 0, 1, 1, 4);
        Image unmuteMusicIcon = Assets.getTile(icons, 1, 0, 1, 1, 4);
        Image muteEffectsIcon = Assets.getTile(icons, 2, 1, 1, 1, 4);
        Image unmuteEffectsIcon = Assets.getTile(icons, 3, 1, 1, 1, 4);

        // Mute settings
        AwesomeButton muteMusic = new AwesomeButton(settings.isMusicMuted() ? unmuteMusicIcon : muteMusicIcon);
        AwesomeButton muteEffects = new AwesomeButton(settings.isEffectsMuted() ? unmuteEffectsIcon : muteEffectsIcon);
        muteMusic.setPreferredSize(new Dimension(32, 32));
        muteEffects.setPreferredSize(new Dimension(32, 32));
        panel.add(muteMusic);
        panel.add(muteEffects);
        layout.putConstraint(SpringLayout.NORTH, muteMusic, 5, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, muteMusic, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, muteEffects, 5, SpringLayout.SOUTH, muteMusic);
        layout.putConstraint(SpringLayout.EAST, muteEffects, -10, SpringLayout.EAST, panel);

        AwesomeEffect.Builder effect = AwesomeEffect.create()
                .addRotationKey(20.0f, 400)
                .addRotationKey(-20.0f, 1200)
                .addRotationKey(0.0f, 1600)
                .addRotationKey(20.0f, 2000)
                .addRotationKey(-20.0f, 2800)
                .addRotationKey(0.0f, 3200)
                .addTranslationYKey(4, 800)
                .addTranslationYKey(-4, 2400)
                .addTranslationYKey(0, 3200).repeats(-1);
        effect.animate(muteMusic, AwesomeEffect.COMPONENT);
        effect.animate(muteEffects, AwesomeEffect.COMPONENT);
        muteMusic.addActionListener(e -> settings.setMuteMusic(!settings.isMusicMuted()) );
        muteEffects.addActionListener(e -> settings.setMuteEffects(!settings.isEffectsMuted()) );

        settings.addListener((property, settings) -> {
            switch (property) {
                case MUTE_MUSIC -> muteMusic.setBackground(settings.isMusicMuted() ? unmuteMusicIcon : muteMusicIcon);
                case MUTE_EFFECTS -> muteEffects.setBackground(settings.isEffectsMuted() ? unmuteEffectsIcon : muteEffectsIcon);
            }
        });

        // Error label
        errorMsg = new JLabel("Error: couldn't connect to the server!");
        errorMsg.setVisible(false);
        errorMsg.setHorizontalAlignment(SwingConstants.CENTER);
        errorMsg.setFont(errorMsg.getFont().deriveFont(20.0f));
        panel.add(errorMsg);
        layout.putConstraint(SpringLayout.WEST, errorMsg, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, errorMsg, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, errorMsg, -100, SpringLayout.SOUTH, panel);

        // Chat
        chat = new Chat(icons);
        panel.add(chat);
        layout.putConstraint(SpringLayout.EAST, chat, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, chat, -10, SpringLayout.SOUTH, panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AwesomeUtil.increaseDelta();
        frame.repaint();
    }

    public void setCurrentPhase(Phase phase) {
        currentPhase = phase;
    }

    public void setContentPanel(Container panel) {
        frame.setContentPane(panel);
        frame.dispatchEvent(new ComponentEvent(frame, ComponentEvent.COMPONENT_RESIZED));
        frame.doLayout();
    }

    public Container getContentPanel() {
        return frame.getContentPane();
    }

    public void setGameCode(String code) {
        gameCode = code;
    }

    public String getGameCode() {
        return gameCode;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    public Settings getSettings() {
        return settings;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void updateUI() {
        frame.pack();
    }

    public void setErrorMsg(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }

    public void hideErrorMsg(String msg) {
        if (errorMsg.getText().equals(msg)) {
            errorMsg.setVisible(false);
        }
    }

    public void sendMessage(Message message) {
        network.sendMessage(message);
    }

    public void sendMessage(Message message, MessageResponseListener responseListener) {
        network.sendMessage(message, responseListener);
    }

    public void receiveMessage(Message msg) {
        if (msg.type == Message.Type.CHAT_MESSAGE) {
            chat.message(msg);
        } else if (msg.type == Message.Type.GOTO) {
            String targetPhase = (String) msg.data.get("phase");
            switch (targetPhase) {
                case "PickWordPhase" -> { setCurrentPhase(new PickWordPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("PickWord");}
                case "DrawPhase" -> { setCurrentPhase(new DrawPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("Draw");}
                case "GuessPhase" -> { setCurrentPhase(new GuessPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("Guess");}
                case "RevealPhase" -> { setCurrentPhase(new RevealPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("Reveal");}
                case "WaitingPhase" -> { setCurrentPhase(new WaitingPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("Waiting");}
                case "WinnerPhase" -> { setCurrentPhase(new WinnerPhase(msg));
                                            audioPlayer.changeSongAudioPlayer("Winner");}
            }
        } else {
            if (currentPhase != null) currentPhase.message(msg);
        }
    }


    public PhaseUI getPhaseUI() {
        return phaseUI;
    }

    public void setGameSettings(GameSettings settings) {
        this.gameSettings = settings;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    private String getJarDir() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        int index = path.lastIndexOf('/');
        return path.substring(0, index);
    }
    @Override
    public void windowClosing(WindowEvent e) {
        settings.setWindowBounds(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
        try {
            System.out.println(getJarDir() + "/settings.ini");
            IniStream.write(settings, new File(getJarDir() + "/settings.ini"));
        } catch (IOException ignored) {}
        try {
            network.closeConnection();
        } catch (Exception ignored) {}
    }

    @Override public void windowOpened(WindowEvent e) { }
    @Override public void windowClosed(WindowEvent e) { }
    @Override public void windowIconified(WindowEvent e) { }
    @Override public void windowDeiconified(WindowEvent e) { }
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}
