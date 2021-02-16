package client;

import client.ui.AwesomeButton;
import client.ui.AwesomeEffect;
import client.ui.AwesomeUtil;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements ActionListener, ComponentListener {
    public static Game game;

    public final Random random = new Random();
    private final JFrame frame;
    private Phase currentPhase;
    private Chat chat;
    private AudioPlayer audioPlayer = new AudioPlayer();

    private Network network;
    private JLabel errorMsg;

    private String gameCode = "---";
    private final List<Player> players = new ArrayList<>();

    Game() {
        // Start the network
        network = new Network();
        network.start();

        // Initiate the window
        frame = new JFrame("Ryktet går!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addComponentListener(this);
        frame.setBackground(new Color(0xe67e22));
        frame.setPreferredSize(new Dimension(1000, 1000));
        frame.setMinimumSize(new Dimension(500, 500));

        // Initiate the layerdpane
        frame.setContentPane(new JLayeredPane());
        initTopLayer();

        // Start with
        setContentPanel(new MainMenu());

        // Move and whow window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start timer for updating graphics
        Timer timer = new Timer(1000 / 30, this);
        timer.setInitialDelay(1000 / 30);
        timer.start();

        game = this;
    }

    private void initTopLayer() {
        JLayeredPane parent = (JLayeredPane) frame.getContentPane();
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);

        // Icons
        BufferedImage icons = Assets.loadImage("ui-icons.png");
        Image muteMusicIcon = Assets.getTile(icons, 0, 0, 1, 1, 4);
        Image unmuteMusicIcon = Assets.getTile(icons, 1, 0, 1, 1, 4);
        Image muteEffectsIcon = Assets.getTile(icons, 2, 1, 1, 1, 4);
        Image unmuteEffectsIcon = Assets.getTile(icons, 3, 1, 1, 1, 4);

        // Mute settings
        JLabel copyRight = new JLabel("Music: www.bensound.com");
        AwesomeButton muteMusic = new AwesomeButton(muteMusicIcon);
        AwesomeButton muteEffects = new AwesomeButton(muteEffectsIcon);
        muteMusic.setPreferredSize(new Dimension(32, 32));
        muteEffects.setPreferredSize(new Dimension(32, 32));
        panel.add(muteMusic);
        panel.add(muteEffects);
        panel.add(copyRight);
        layout.putConstraint(SpringLayout.NORTH, copyRight, 5, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, copyRight, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, muteMusic, 5, SpringLayout.SOUTH, copyRight);
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
        panel.add(errorMsg);
        layout.putConstraint(SpringLayout.WEST, errorMsg, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, errorMsg, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, errorMsg, -100, SpringLayout.SOUTH, panel);

        // Chat
        chat = new Chat(icons);
        panel.add(chat);
        layout.putConstraint(SpringLayout.EAST, chat, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, chat, -10, SpringLayout.SOUTH, panel);

        parent.add(panel, JLayeredPane.POPUP_LAYER);
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
        Component[] components = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component comp : components) {
            layeredPane.remove(comp);
        }
        layeredPane.add(panel, JLayeredPane.DEFAULT_LAYER);
        frame.pack();
    }

    public String getGameCode() {
        return gameCode;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
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

    @Override
    public void componentResized(ComponentEvent e) {
        Container container = frame.getContentPane();
        Dimension size = container.getSize();
        AwesomeUtil.updateFonts(Math.min(size.width, size.height));
        for (Component comp : container.getComponents()) {
            comp.setSize(size.width, size.height);
        }
    }

    @Override public void componentMoved(ComponentEvent e) { }
    @Override public void componentShown(ComponentEvent e) { }
    @Override public void componentHidden(ComponentEvent e) { }
}
