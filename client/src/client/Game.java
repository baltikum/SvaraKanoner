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

public class Game extends JFrame implements ActionListener, ComponentListener {
    public static Game game;

    public final Random random = new Random();
    private Phase currentPhase;

    private Network network;

    private String gameCode = "---";
    private List<Player> players = new ArrayList<>();

    Game() {
        addComponentListener(this);

        // Start the network
        network = new Network();
        network.start();

        setTitle("Ryktet går!");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setContentPane(new MainMenu());
        setBackground(new Color(0xe67e22));

        setPreferredSize(new Dimension(1000, 1000));
        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        Timer timer = new Timer(1000 / 100, this);
        timer.setInitialDelay(1000 / 100);
        timer.start();

        game = this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AwesomeUtil.increaseDelta();
        repaint();
    }

    public void setCurrentPhase(Phase phase) {
        currentPhase = phase;
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
        AwesomeUtil.updateFonts(Math.min(getWidth(), getHeight()));
    }

    @Override
    public void componentMoved(ComponentEvent e) { }
    @Override
    public void componentShown(ComponentEvent e) { }
    @Override
    public void componentHidden(ComponentEvent e) { }

    public void sendMessage(Message message) {
        network.sendMessage(message);
    }
}
