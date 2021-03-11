package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Handles all permanent ui/data from when the game is started until the user closes it.
 * It's a singleton and can be retrieved with getInstance after constructed.
 *
 * It owns the window, network and game session if one has been joined/created.
 *
 * @author Jesper Jansson
 * @version 03/03/21
 */
public class Game implements ActionListener, WindowListener {

    private static Game instance;
    public static Game getInstance() {
        return instance;
    }

    private final JFrame frame;

    private GameSession session;
    private Network network;
    private JLabel errorMsg;
    private Chat chat;

    /**
     * Constructs the singleton game instance.
     * And the window for the game and any permanent ui.
     */
    public Game() {
        Settings settings = Settings.getSettings();
        Rectangle windowBounds = settings.getWindowBounds();

        // Initiate the window
        frame = new JFrame("Draw & Guess");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setBackground(new Color(0xe67e22));
        frame.setPreferredSize(new Dimension(windowBounds.width, windowBounds.height));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setPreferredSize(new Dimension(800, 800));

        // Initiate the LayeredPane
        initTopLayer();

        // Start in the main menu
        setContentPanel(new MainMenu(this));

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

        instance = this;
    }

    /**
     * Opens a connection to the server.
     */
    public void establishConnection(Network.ConnectedListener listener) {
        if (network != null) network.closeConnection();
        network = new Network(this, listener);
        network.start();
    }

    /**
     * Initiate the ui to be displayed above everything else.
     */
    private void initTopLayer() {
        Settings settings = Settings.getSettings();
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout) {
            @Override
            public void paintComponent(Graphics g) {}
        };
        frame.setGlassPane(panel);
        panel.setVisible(true);
        panel.setOpaque(false);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setCursor(null);
        panel.setEnabled(false);

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

        settings.addListener((property, clientSettings) -> {
            switch (property) {
                case MUTE_MUSIC -> muteMusic.setBackground(clientSettings.isMusicMuted() ? unmuteMusicIcon : muteMusicIcon);
                case MUTE_EFFECTS -> muteEffects.setBackground(clientSettings.isEffectsMuted() ? unmuteEffectsIcon : muteEffectsIcon);
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

    /**
     * Sets the content panel of the window.
     * @param panel The new content panel.
     */
    public void setContentPanel(Container panel) {
        frame.setContentPane(panel);
        frame.revalidate();
    }

    /**
     * Sets a error message to be displayed to the player.
     * @param msg The message to display.
     */
    public void setErrorMsg(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
    }

    /**
     * Sends a message to the server.
     * @param message The message to send.
     */
    public void sendMessage(Message message) {
        network.sendMessage(message);
    }

    /**
     *
     * @return The network instance for the game.
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Sends a message to the server and registers a response listener.
     * @param message The message to send.
     * @param responseListener The response listener to register.
     */
    public void sendMessage(Message message, MessageResponseListener responseListener) {
        network.sendMessage(message, responseListener);
    }

    /**
     *
     * @return The chat.
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * @return The session if one has been joined/created else null.
     */
    public GameSession getSession() {
        return session;
    }

    /**
     * Starts a new session.
     * @param thisPlayer The player data of this client.
     * @param gameSettings The game settings for the session.
     * @param sessionId The id of the session.
     */
    public void startSession(Player thisPlayer, GameSettings gameSettings, String sessionId) {
        session = new GameSession(thisPlayer, gameSettings, sessionId);
    }

    /**
     * Leaves the current session resulting in getSession returning null until a new i started.
     */
    public void leaveSession() {
        chat.clear();
        session = null;
        setContentPanel(new MainMenu(this));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AwesomeUtil.increaseDelta();
        frame.repaint();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Settings.getSettings().setWindowBounds(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
        Settings.saveSettings();
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
