package client;


import client.Assets;
import client.ui.AwesomeIconLabel;
import common.Player;

import javax.swing.*;
import java.awt.*;

public class PhaseUI {

    JPanel panel;
    JPanel playersPanel;
    JPanel phaseContent;

    public PhaseUI() {
        panel = new JPanel();
        phaseContent = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xe67e22));
        panel.setLayout(new BorderLayout());


        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);
        playersPanel.setBackground(new Color(0xe67e22));

        for(Player player : Game.game.getPlayers()) {
            addPlayerToList(player);
        }


        panel.add(playersPanel, BorderLayout.LINE_START);
        panel.add(phaseContent, BorderLayout.CENTER);

        Game.game.setContentPanel(panel);
    }

    public void setContent(JPanel content) {
        panel.remove(phaseContent);
        panel.add(content);
        phaseContent = content;
        phaseContent.revalidate();


    }

    private void addPlayerToList(Player player) {
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(Assets.getPlayerIcons()[player.getAvatarId()], player.getName());
        playerLabel.setPreferredSize(new Dimension(175, 40));
        playerLabel.setMaximumSize(new Dimension(175, 40));


        playersPanel.add(Box.createVerticalStrut(10));


        playersPanel.add(playerLabel);
    }
}
