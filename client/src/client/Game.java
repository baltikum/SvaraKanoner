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

    private boolean isMuted = false;

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
        Image muteIcon = Assets.getTile(icons, 0, 0, 1, 1, 4);
        Image unmuteIcon = Assets.getTile(icons, 1, 0, 1, 1, 4);

        // Mute settings
        AwesomeButton mute = new AwesomeButton(muteIcon);
        mute.setPreferredSize(new Dimension(32, 32));
        panel.add(mute);
        layout.putConstraint(SpringLayout.NORTH, mute, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, mute, -10, SpringLayout.EAST, panel);

        AwesomeEffect.create()
                .addRotationKey(20.0f, 400)
                .addRotationKey(-20.0f, 1200)
                .addRotationKey(0.0f, 1600)
                .addTranslationYKey(4, 400)
                .addTranslationYKey(-4, 1200)
                .addTranslationYKey(0, 1600).repeats(-1).animate(mute, AwesomeEffect.COMPONENT);

        mute.addActionListener(e -> {
            mute.setBackground(isMuted ? muteIcon : unmuteIcon);
            isMuted = !isMuted;
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

    @Override
    public void componentResized(ComponentEvent e) {
        Container container = frame.getContentPane();
        AwesomeUtil.updateFonts(Math.min(container.getWidth(), container.getHeight()));
        for (Component comp : container.getComponents()) {
            comp.setBounds(0, 0, container.getWidth(), container.getHeight());
        }
    }

    public void sendMessage(Message message) {
        network.sendMessage(message);
    }

    @Override public void componentMoved(ComponentEvent e) { }
    @Override public void componentShown(ComponentEvent e) { }
    @Override public void componentHidden(ComponentEvent e) { }
}
