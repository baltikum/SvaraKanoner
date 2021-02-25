package client;

import client.ui.*;
import common.*;


import javax.swing.*;
import javax.swing.text.Utilities;
import java.awt.*;
import java.util.List;

public class PickWordPhase extends Phase {

    JPanel panel;
    JPanel playersPanel;
    JPanel wordsPanel;
    PercentLayout percentLayout;

    boolean pressedWord = false;

    public PickWordPhase(PhaseUI phaseUI) {
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(0xe67e22));
        panel.setLayout(new BorderLayout());

        percentLayout = new PercentLayout(1.0f);

        AwesomeText header = new AwesomeText("Pick a word!");
        header.setTextColor(Color.black);
        AwesomeUtil.dynamicFont(header, 0.2f);
        percentLayout.setConstraintsRatioByWidth(header, 0.5f, 0.1f, 0.7f, 0.7f);


        wordsPanel = new JPanel(percentLayout);
        wordsPanel.setOpaque(false);
        wordsPanel.setBackground(new Color(0xe67e22));




        panel.add(header, BorderLayout.CENTER);
        panel.add(wordsPanel, BorderLayout.CENTER);


        phaseUI.setContent(panel);
/*
        Message message = new Message(Message.Type.GET_WORD_CHOICES);
        Game.game.sendMessage(message, new MessageResponseListener() {
            @Override
            public void onSuccess(Message msg) {
                String[] words = (String[])msg.data.get("words");

                for (int i = 0; i < words.length; i++) {
                    addWordToList(words[i],i, 0.75f, 0.6f);
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });*/

    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SEND_WORD_CHOICES -> {
                String[] words = (String[])msg.data.get("words");
                /*
                for (int i = 0; i < words.length; i++) {
                    addWordToList(words[i],i, 0.75f, 0.6f);
                   }
                 */
                // fixa bättre grid layout eller alltid ha 4 valbara ord
                addWordToList(words[0],0, 0.25f, 0.3f);
                addWordToList(words[1],1, 0.75f, 0.3f);
                addWordToList(words[2],2, 0.25f, 0.6f);
                addWordToList(words[3],3, 0.75f, 0.6f);
            }
        }

    }



    private void addWordToList(String word,int index, float x, float y) {
        AwesomeButton wordButton = new AwesomeButton(word, Assets.getButtonIcon());
        AwesomeUtil.dynamicFont(wordButton, 0.5f);
        percentLayout.setConstraintsRatioByWidth(wordButton, x, y, 0.4f, 0.4f);

        AwesomeUtil.wiggleOnHover(wordButton, 20);

        wordsPanel.add(wordButton);
        //wordsPanel.revalidate();

        wordButton.addActionListener(e -> {
            if (pressedWord) return;
            System.out.println("pressed word "+ word + index);
            Message message = new Message(Message.Type.PICK_WORD);
            message.addParameter("wordIndex", index);
            Game.game.sendMessage(message);

            panel.remove(wordsPanel);
        });

    }
}
