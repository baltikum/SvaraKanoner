package client;


import client.ui.AwesomeIconLabel;
import client.ui.AwesomeText;
import client.ui.AwesomeUtil;
import common.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;


/**
 * Handles UI that is used by multiple phases,
 * such as the players panel, header and timer
 *
 * @author Lukas Magnusson
 * @version 05/03/21
 */
public class PhaseUI {

    JPanel panel;
    JPanel playersPanel;
    JPanel phaseContent;
    JPanel titlePanel;
    AwesomeText timeLeftText;
    AwesomeText title;
    final HashMap<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();

    int secondsLeft;

    Timer timer;

    /**
     * setup UI
     */
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

        title = new AwesomeText("");
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

    /**
     *
     *
     * @param content the phase content displayed in the center of the screen
     */
    public void setContent(JPanel content) {
        Game.getInstance().setContentPanel(panel);
        panel.remove(phaseContent);
        panel.add(content, BorderLayout.CENTER);
        phaseContent = content;
        phaseContent.revalidate();
    }

    /**
     *
     * @param text title
     */
    public void setTitle(String text) {
        title.setText(text);
    }

    /**
     * start a countdown timer
     * @param seconds timer start value
     */
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

    /**
     * stops the timer
     */
    public void stopTimer() {
        if (timer != null)
            timer.stop();
    }

    /**
     * hides the timer
     */
    public void hideTimer() {
        stopTimer();
        timeLeftText.setText("");
    }

    /**
     * Set the color of the player panel in the players list
     *
     * @param playerId
     * @param color
     */
    public void setColorOfPlayer(int playerId, Color color) {
        AwesomeIconLabel label = playerIdToLabel.get(playerId);
        if (label != null) {
            label.setTextColor(color);
        }
    }

    /**
     * resets the player list to default colors
     */
    public void resetPlayerColors() {
        for (AwesomeIconLabel label : playerIdToLabel.values()) {
            label.setTextColor(Color.BLACK);
        }
    }

    /**
     * adds a player to the players list
     * @param player
     */
    public void addPlayerToList(Player player) {
        if (playerIdToLabel.containsKey(player.getId())) return;

        AwesomeIconLabel playerLabel = new AwesomeIconLabel(Assets.getAvatarImage(player.getAvatarId()), player.getName());
        playerLabel.setPreferredSize(new Dimension(175, 40));
        playerLabel.setMaximumSize(new Dimension(175, 40));

        playersPanel.add(Box.createVerticalStrut(10));
        playersPanel.add(playerLabel, BorderLayout.AFTER_LAST_LINE);
        playerIdToLabel.put(player.getId(), playerLabel);
    }

    /**
     * removes a player from the players list
     * @param player
     */
    public void removePlayerFromList(Player player) {
        AwesomeIconLabel playerLabel = playerIdToLabel.get(player.getId());
        if (playerLabel != null) {
            playersPanel.remove(playerLabel);
        }
    }
}
