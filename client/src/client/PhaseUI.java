package client;


import client.Assets;
import client.ui.AwesomeIconLabel;
import client.ui.AwesomeText;
import client.ui.AwesomeUtil;
import client.ui.PercentLayout;
import common.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


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
    JPanel timerPanel;

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



/*
        AwesomeText header = new AwesomeText("Header Test!");
        header.setTextColor(Color.black);
        AwesomeUtil.dynamicFont(header, 0.2f);

        PercentLayout percentLayout = new PercentLayout(1.0f);
        percentLayout.setConstraintsRatioByWidth(header, 0.0f, -0.4f, 0.7f, 0.7f);
        //percentLayout.setConstraintsRatioByWidth(header, 0.5f, 0.1f, 0.7f, 0.7f);
*/

        timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        timerPanel.setOpaque(false);
        timerPanel.setBackground(new Color(0xe67e22));

/*
        PercentLayout percentLayout = new PercentLayout(1.0f);

        AwesomeText header = new AwesomeText("Pick a word!");
        header.setTextColor(Color.black);
        AwesomeUtil.dynamicFont(header, 0.2f);
        percentLayout.setConstraintsRatioByWidth(header, 0.5f, 0.1f, 0.7f, 0.7f);*/

        //PercentLayout percentLayout = new PercentLayout(1.0f);
        AwesomeText timeLeftText = new AwesomeText("Time left 10");
        timeLeftText.setPreferredSize(new Dimension(200, 20));
        timeLeftText.setMaximumSize(new Dimension(200, 20));

        //timeLeftText.setPreferredSize(new Dimension(10, 10));

        //percentLayout.setConstraintsRatioByWidth(timeLeftText, -0.5f, -0.1f, 0.7f, 0.7f);
        //panel.add(timeLeftText, BorderLayout.SOUTH);


        //timerPanel.add(timeLeftText, BorderLayout.LINE_START);
        playersPanel.add(timeLeftText, BorderLayout.SOUTH);



        panel.add(playersPanel, BorderLayout.LINE_START);


        //panel.add(timerPanel, BorderLayout.AFTER_LAST_LINE);

        //panel.add(header, BorderLayout.NORTH);

        panel.add(phaseContent, BorderLayout.CENTER);

        Game.game.setContentPanel(panel);
    }

    public void setContent(JPanel content) {
        panel.remove(phaseContent);
        panel.add(content, BorderLayout.CENTER);
        phaseContent = content;
        phaseContent.revalidate();


    }

    private void addPlayerToList(Player player) {
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(Assets.getPlayerIcons()[player.getAvatarId()], player.getName());
        playerLabel.setPreferredSize(new Dimension(175, 40));
        playerLabel.setMaximumSize(new Dimension(175, 40));


        playersPanel.add(Box.createVerticalStrut(10));


        playersPanel.add(playerLabel, BorderLayout.AFTER_LAST_LINE);
    }
}
