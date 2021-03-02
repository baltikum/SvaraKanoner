package client;


import client.ui.AwesomeIconLabel;
import client.ui.AwesomeText;
import client.ui.AwesomeUtil;
import common.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


/**
 * Handles UI that is used by multiple phases,
 * such as the players panel, header and timer
 *
 * @author Lukas Magnusson
 */
public class PhaseUI {

    JPanel panel;
    JPanel playersPanel;
    JPanel phaseContent;
    JPanel titlePanel;
    AwesomeText timeLeftText;
    AwesomeText title;

    int secondsLeft;

    Timer timer;

    public PhaseUI() {

        panel = new JPanel();
        phaseContent = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xE67E22));
        panel.setLayout(new BorderLayout());


        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);
        playersPanel.setBackground(new Color(0xe67e22));


        for(Player player : Game.game.getPlayers()) {
            addPlayerToList(player);
        }


        SpringLayout layout = new SpringLayout();

        titlePanel = new JPanel();
        titlePanel.setLayout(layout);
        titlePanel.setOpaque(true);
        titlePanel.setBackground(new Color(0xe67e22));

        panel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                titlePanel.setPreferredSize((new Dimension(0, (int)(panel.getHeight() * 0.1f))));
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        timeLeftText = new AwesomeText("");

        title = new AwesomeText("Pick a woprd!1!!");
        titlePanel.add(timeLeftText);
        titlePanel.add(title);
        layout.putConstraint(SpringLayout.WEST, timeLeftText, 30, SpringLayout.WEST, titlePanel);
        layout.putConstraint(SpringLayout.EAST, timeLeftText, 50, SpringLayout.WEST, titlePanel);
        layout.putConstraint(SpringLayout.NORTH, timeLeftText, 0, SpringLayout.NORTH, titlePanel);
        layout.putConstraint(SpringLayout.SOUTH, timeLeftText, 0, SpringLayout.SOUTH, titlePanel);

        AwesomeUtil.dynamicFont(timeLeftText, 0.8f);

        layout.putConstraint(SpringLayout.WEST, title, 0, SpringLayout.WEST, titlePanel);
        layout.putConstraint(SpringLayout.EAST, title, 0, SpringLayout.EAST, titlePanel);
        layout.putConstraint(SpringLayout.NORTH, title, 0, SpringLayout.NORTH, titlePanel);
        layout.putConstraint(SpringLayout.SOUTH, title, 0, SpringLayout.SOUTH, titlePanel);

        AwesomeUtil.dynamicFont(title, 0.8f);

        panel.add(playersPanel, BorderLayout.LINE_START);
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(phaseContent, BorderLayout.CENTER);
    }

    public void setContent(JPanel content) {
        Game.game.setContentPanel(panel);
        panel.remove(phaseContent);
        panel.add(content, BorderLayout.CENTER);
        phaseContent = content;
        phaseContent.revalidate();
    }

    public void setTitle(String text) {
        title.setText(text);
    }
    public void startTimer(int seconds) {
        stopTimer();
        secondsLeft = seconds;
        timeLeftText.setText(String.valueOf(secondsLeft));
        timer = new Timer(1000, e ->  {
            timeLeftText.setText(String.valueOf(--secondsLeft));
            if (secondsLeft <= 0) {
                timer.stop();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.stop();
    }

    public void hideTimer() {
        stopTimer();
        timeLeftText.setText("");
    }

    private void addPlayerToList(Player player) {
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(Assets.getPlayerIcons()[player.getAvatarId()], player.getName());
        playerLabel.setPreferredSize(new Dimension(175, 40));
        playerLabel.setMaximumSize(new Dimension(175, 40));


        playersPanel.add(Box.createVerticalStrut(10));


        playersPanel.add(playerLabel, BorderLayout.AFTER_LAST_LINE);
    }
}
