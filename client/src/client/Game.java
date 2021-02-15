package client;

import client.ui.AwesomeUtil;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements ActionListener, ComponentListener {
    public static Game game;

    public final Random random = new Random();
    private final JFrame frame;
    private Phase currentPhase;

    private Network network;

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

        // Start with
        frame.setContentPane(new MainMenu());
        frame.setBackground(new Color(0xe67e22));

        frame.setPreferredSize(new Dimension(1000, 1000));
        frame.setMinimumSize(new Dimension(500, 500));
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(1000 / 30, this);
        timer.setInitialDelay(1000 / 30);
        timer.start();

        game = this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AwesomeUtil.increaseDelta();
        frame.repaint();
    }

    public void setCurrentPhase(Phase phase) {
        currentPhase = phase;
    }

    public void setContentPanel(JPanel panel) {
        frame.setContentPane(panel);
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

    @Override
    public void componentResized(ComponentEvent e) {
        AwesomeUtil.updateFonts(Math.min(frame.getWidth(), frame.getHeight()));
    }

    public void sendMessage(Message message) {
        network.sendMessage(message);
    }

    @Override public void componentMoved(ComponentEvent e) { }
    @Override public void componentShown(ComponentEvent e) { }
    @Override public void componentHidden(ComponentEvent e) { }
}
