package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
            IniStream.read(settings, new File(Assets.getResourcesPath() + "settings.ini"));
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
        frame.setContentPane(new JLayeredPane());
        initTopLayer();

        // Start the network
        network = new Network(settings);
        network.start();

        // Start with
        setCurrentPhase(new MainMenu());
        // Message msg = new Message(Message.Type.PICK_WORD);
        // msg.addParameter("words", new String[]{"aaa", "aaa", "asd", "aasd"});
        // setCurrentPhase(new PickWordPhase(msg));
        // setCurrentPhase(new WinnerPhase(new Message(Message.Type.GOTO)));
        // setCurrentPhase(new DrawPhase());
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
        JLayeredPane parent = (JLayeredPane) frame.getContentPane();
        parent.setLayout(layout);

        // Icons
        BufferedImage icons = Assets.loadImage("ui-icons.png");
        Image muteMusicIcon = Assets.getTile(icons, 0, 0, 1, 1, 4);
        Image unmuteMusicIcon = Assets.getTile(icons, 1, 0, 1, 1, 4);
        Image muteEffectsIcon = Assets.getTile(icons, 2, 1, 1, 1, 4);
        Image unmuteEffectsIcon = Assets.getTile(icons, 3, 1, 1, 1, 4);

        // Mute settings
        JLabel copyRight = new JLabel("Music: www.bensound.com");
        AwesomeButton muteMusic = new AwesomeButton(settings.isMusicMuted() ? unmuteMusicIcon : muteMusicIcon);
        AwesomeButton muteEffects = new AwesomeButton(settings.isEffectsMuted() ? unmuteEffectsIcon : muteEffectsIcon);
        muteMusic.setPreferredSize(new Dimension(32, 32));
        muteEffects.setPreferredSize(new Dimension(32, 32));
        parent.add(muteMusic, JLayeredPane.POPUP_LAYER);
        parent.add(muteEffects, JLayeredPane.POPUP_LAYER);
        parent.add(copyRight, JLayeredPane.POPUP_LAYER);
        layout.putConstraint(SpringLayout.NORTH, copyRight, 5, SpringLayout.NORTH, parent);
        layout.putConstraint(SpringLayout.EAST, copyRight, -10, SpringLayout.EAST, parent);
        layout.putConstraint(SpringLayout.NORTH, muteMusic, 5, SpringLayout.SOUTH, copyRight);
        layout.putConstraint(SpringLayout.EAST, muteMusic, -10, SpringLayout.EAST, parent);
        layout.putConstraint(SpringLayout.NORTH, muteEffects, 5, SpringLayout.SOUTH, muteMusic);
        layout.putConstraint(SpringLayout.EAST, muteEffects, -10, SpringLayout.EAST, parent);

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
        parent.add(errorMsg, JLayeredPane.POPUP_LAYER);
        layout.putConstraint(SpringLayout.WEST, errorMsg, 10, SpringLayout.WEST, parent);
        layout.putConstraint(SpringLayout.EAST, errorMsg, -10, SpringLayout.EAST, parent);
        layout.putConstraint(SpringLayout.SOUTH, errorMsg, -100, SpringLayout.SOUTH, parent);

        // Chat
        chat = new Chat(icons);
        parent.add(chat, JLayeredPane.POPUP_LAYER);
        layout.putConstraint(SpringLayout.EAST, chat, -10, SpringLayout.EAST, parent);
        layout.putConstraint(SpringLayout.SOUTH, chat, -10, SpringLayout.SOUTH, parent);
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
        JLayeredPane layeredPane = (JLayeredPane) frame.getContentPane();
        SpringLayout layout = (SpringLayout) layeredPane.getLayout();

        Component[] components = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component comp : components) {
            layeredPane.remove(comp);
        }
        layeredPane.add(panel, JLayeredPane.DEFAULT_LAYER);

        layout.putConstraint(SpringLayout.WEST, panel, 0, SpringLayout.WEST, layeredPane);
        layout.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, layeredPane);
        layout.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, layeredPane);
        layout.putConstraint(SpringLayout.SOUTH, panel, 0, SpringLayout.SOUTH, layeredPane);

        frame.dispatchEvent(new ComponentEvent(frame, ComponentEvent.COMPONENT_RESIZED));
        frame.doLayout();
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
                case "PickWordPhase" -> setCurrentPhase(new PickWordPhase(msg));
                case "DrawPhase" -> setCurrentPhase(new DrawPhase(msg));
                case "GuessPhase" -> setCurrentPhase(new GuessPhase(msg));
                case "RevealPhase" -> setCurrentPhase(new RevealPhase(msg));
                case "WaitingPhase" -> setCurrentPhase(new WaitingPhase(msg));
                case "WinnerPhase" -> setCurrentPhase(new WinnerPhase(msg));
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

    @Override
    public void windowClosing(WindowEvent e) {
        settings.setWindowBounds(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
        try {
            IniStream.write(settings, new File(Assets.getResourcesPath() + "settings.ini"));
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
