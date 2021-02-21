package client;

import client.ui.*;
import common.Message;
import common.Phase;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.awt.*;
import java.util.List;

public class PickWordPhase extends Phase {

    JPanel panel;
    JPanel playersPanel;
    JPanel wordsPanel;

    public PickWordPhase() {
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xe67e22));
        panel.setLayout(new BorderLayout());
        //panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setOpaque(false);
        playersPanel.setBackground(new Color(0xe67e22));


        addPlayerToList(new Player(0, "Jesper", Assets.getPlayerIcons()[0]));
        addPlayerToList(new Player(1, "Mattias", Assets.getPlayerIcons()[2]));
        addPlayerToList(new Player(2, "Lukas", Assets.getPlayerIcons()[4]));
        addPlayerToList(new Player(3, "Johnny", Assets.getPlayerIcons()[6]));



        PercentLayout percentLayout = new PercentLayout(1.0f);

        AwesomeText header = new AwesomeText("Pick a word!", AwesomeUtil.BIG_TEXT);
        percentLayout.setConstraintsRatioByWidth(header, 0.65f, 0.1f, 0.8f, 0.8f);


        wordsPanel = new JPanel(percentLayout);
        wordsPanel.setOpaque(false);
        wordsPanel.setBackground(new Color(0xe67e22));



        addWordToList("Dog", percentLayout, 0.25f, 0.3f);
        addWordToList("Rock",percentLayout, 0.75f, 0.3f);
        addWordToList("Mouse",percentLayout, 0.25f, 0.6f);
        addWordToList("Cat",percentLayout, 0.75f, 0.6f);

        //wordsPanel.setMaximumSize(new Dimension(100, 100));


        panel.add(playersPanel, BorderLayout.LINE_START);
        panel.add(header, BorderLayout.CENTER);
        panel.add(wordsPanel, BorderLayout.CENTER);



        Game.game.setContentPanel(panel);


    }

    @Override
    public void message(Message msg) {

    }

    private void addPlayerToList(Player player) {
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(player.getAvatar(), player.getName());
        playerLabel.setPreferredSize(new Dimension(175, 40));
        playerLabel.setMaximumSize(new Dimension(175, 40));


        playersPanel.add(Box.createVerticalStrut(10));


        playersPanel.add(playerLabel);
    }

    private void addWordToList(String word, PercentLayout percentLayout, float x, float y) {
        AwesomeButton wordButton = new AwesomeButton(word, Assets.getButtonIcon(), AwesomeUtil.BIG_TEXT);

        percentLayout.setConstraintsRatioByWidth(wordButton, x, y, 0.4f, 0.4f);

        AwesomeUtil.wiggleOnHover(wordButton, 20);

        wordsPanel.add(wordButton);

        wordButton.addActionListener(e -> {
            System.out.println("pressed word "+ word);
        });

    }
}
