package client;

import client.ui.AwesomeUtil;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Game extends JFrame implements ActionListener {
    private Phase currentPhase;

    private Network network;

    Game() {

        // connect to server
        try {
            network = new Network();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AwesomeUtil.increaseDelta();
        repaint();
    }
}
