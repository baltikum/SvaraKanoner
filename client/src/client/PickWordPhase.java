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
    private final GameSession session;

    boolean pressedWord = false;

    public PickWordPhase(Message msg) {
        session = Game.getInstance().getSession();
        PhaseUI phaseUI = session.getPhaseUI();

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xe67e22));
        panel.setLayout(new BorderLayout());

        percentLayout = new PercentLayout(1.0f);
        wordsPanel = new JPanel(percentLayout);
        wordsPanel.setOpaque(false);
        wordsPanel.setBackground(new Color(0xe67e22));


        String[] words = (String[])msg.data.get("words");

        addWordToList(words[0],0, 0.25f, 0.3f);
        addWordToList(words[1],1, 0.75f, 0.3f);
        addWordToList(words[2],2, 0.25f, 0.6f);
        addWordToList(words[3],3, 0.75f, 0.6f);

        panel.add(wordsPanel, BorderLayout.CENTER);

        phaseUI.setTitle("Pick a word!");
        phaseUI.setContent(panel);
        phaseUI.startTimer((int)session.getGameSettings().getPickTimeMilliseconds() / 1000);
    }

    @Override
    public void message(Message msg) { }

    /**
     * Add UI for a word that the client can pick.
     *
     * When word is clicked the client sends a message containing the word index to the server
     *
     * @param word the word to add
     * @param index the word index
     * @param x position x
     * @param y position y
     */
    private void addWordToList(String word,int index, float x, float y) {
        AwesomeButton wordButton = new AwesomeButton(word, Assets.getMainmenuIcon(Assets.MENU_WHAM));
        AwesomeUtil.dynamicFont(wordButton, 0.5f);
        percentLayout.setConstraintsRatioByWidth(wordButton, x, y, 0.4f, 0.4f);

        AwesomeUtil.wiggleOnHover(wordButton, 20);

        wordsPanel.add(wordButton);

        wordButton.addActionListener(e -> {
            if (pressedWord) return;
            System.out.println("pressed word "+ word + index);
            Message message = new Message(Message.Type.PICK_WORD);
            message.addParameter("wordIndex", index);
            Game.getInstance().sendMessage(message);

            panel.remove(wordsPanel);
            session.getPhaseUI().setTitle("Waiting on players...");
        });
    }
}
