package client;

import client.ui.*;
import common.*;


import javax.swing.*;
import java.awt.*;


/**
 * Client side of the pick word phase
 *
 * gets word options from server
 *
 * sends the chosen word back to server
 *
 * @author Lukas Magnusson
 */

public class PickWordPhase extends Phase {

    JPanel panel;
    JPanel wordsPanel;
    PercentLayout percentLayout;
    AwesomeText header;

    boolean pressedWord = false;

    public PickWordPhase(Message msg) {
        PhaseUI phaseUI = new PhaseUI();

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xe67e22));
        panel.setLayout(new BorderLayout());

        percentLayout = new PercentLayout(1.0f);

        header = new AwesomeText("Pick a word!");
        header.setTextColor(Color.black);
        AwesomeUtil.dynamicFont(header, 0.2f);
        percentLayout.setConstraintsRatioByWidth(header, 0.5f, 0.1f, 0.7f, 0.7f);


        wordsPanel = new JPanel(percentLayout);
        wordsPanel.setOpaque(false);
        wordsPanel.setBackground(new Color(0xe67e22));


        String[] words = (String[])msg.data.get("words");

        // fixa bättre grid layout eller alltid ha 4 valbara ord
        addWordToList(words[0],0, 0.25f, 0.3f);
        addWordToList(words[1],1, 0.75f, 0.3f);
        addWordToList(words[2],2, 0.25f, 0.6f);
        addWordToList(words[3],3, 0.75f, 0.6f);


        panel.add(header, BorderLayout.CENTER);
        panel.add(wordsPanel, BorderLayout.CENTER);


        phaseUI.setContent(panel);

    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {

        }

    }



    private void addWordToList(String word,int index, float x, float y) {
        AwesomeButton wordButton = new AwesomeButton(word, Assets.getButtonIcon());
        AwesomeUtil.dynamicFont(wordButton, 0.5f);
        percentLayout.setConstraintsRatioByWidth(wordButton, x, y, 0.4f, 0.4f);

        AwesomeUtil.wiggleOnHover(wordButton, 20);

        wordsPanel.add(wordButton);

        wordButton.addActionListener(e -> {
            if (pressedWord) return;
            System.out.println("pressed word "+ word + index);
            Message message = new Message(Message.Type.PICK_WORD);
            message.addParameter("wordIndex", index);
            Game.game.sendMessage(message);

            panel.remove(wordsPanel);
            header.setText("Waiting on players...");
        });

    }
}
