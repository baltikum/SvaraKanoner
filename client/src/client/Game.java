package client;

import client.ui.AwesomeButton;
import client.ui.AwesomeEffect;
import client.ui.AwesomeUtil;
import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements ActionListener, WindowListener {
    public static Game game;

    public final Random random = new Random();
    private final JFrame frame;
    private Phase currentPhase;

    private final Settings settings = new Settings();
    private Chat chat;
    private final AudioPlayer audioPlayer;

    private Network network;
    private JLabel errorMsg;

    private String gameCode = "---";
    private final Player thisPlayer = new Player(-1, "Bengt", 0);
    private final List<Player> players = new ArrayList<>();

    Game() {
        game = this;

        try {
            IniStream.read(settings, new File(Assets.getResourcesPath() + "settings.ini"));
            settings.validate();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        audioPlayer = new AudioPlayer();

        // Start the network
        network = new Network();
        network.start();

        // Initiate the window
        frame = new JFrame("Ryktet går!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setBackground(new Color(0xe67e22));
        frame.setPreferredSize(new Dimension(settings.windowWidth, settings.windowHeight));
        frame.setMinimumSize(new Dimension(500, 500));

        // Initiate the LayeredPane
        frame.setContentPane(new JLayeredPane());
        initTopLayer();

        // Start with
        setCurrentPhase(new MainMenu());

        // Move and show window
        if (settings.windowPositionX < 0 || settings.windowPositionY < 0)
            frame.setLocationRelativeTo(null);
        else
            frame.setLocation(settings.windowPositionX, settings.windowPositionY);
        frame.setVisible(true);

        // Start timer for updating graphics
        Timer timer = new Timer(1000 / 30, this);
        timer.setInitialDelay(1000 / 30);
        timer.start();

        // test a phase
        //setCurrentPhase(new PickWordPhase());
        //setCurrentPhase(new );
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
        AwesomeButton muteMusic = new AwesomeButton(settings.muteMusic ? unmuteMusicIcon : muteMusicIcon);
        AwesomeButton muteEffects = new AwesomeButton(settings.muteEffects ? unmuteEffectsIcon : muteEffectsIcon);
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

        muteMusic.addActionListener(e -> {
            muteMusic.setBackground(audioPlayer.isMusicMuted() ? muteMusicIcon : unmuteMusicIcon);
            if (audioPlayer.isMusicMuted()) audioPlayer.unmuteMusic(); else audioPlayer.muteMusic();
        });
        muteEffects.addActionListener(e -> {
            muteEffects.setBackground(audioPlayer.isEffectsMuted() ? muteEffectsIcon : unmuteEffectsIcon);
            if (audioPlayer.isEffectsMuted()) audioPlayer.unmuteEffects(); else audioPlayer.muteEffects();
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
        switch (msg.type) {
            // TODO: Add chat messages here.
            default -> {
                if (currentPhase != null) currentPhase.message(msg);
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        settings.windowPositionX = frame.getX();
        settings.windowPositionY = frame.getY();
        settings.windowWidth = frame.getWidth();
        settings.windowHeight = frame.getHeight();
        try {
            IniStream.write(settings, new File(Assets.getResourcesPath() + "settings.ini"));
        } catch (IOException ignored) {}
    }

    @Override public void windowOpened(WindowEvent e) { }
    @Override public void windowClosed(WindowEvent e) { }
    @Override public void windowIconified(WindowEvent e) { }
    @Override public void windowDeiconified(WindowEvent e) { }
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}
