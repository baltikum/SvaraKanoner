package client;

import client.ui.AwesomeUtil;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JFrame implements ActionListener {
    private Phase currentPhase;

    Game() {
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
