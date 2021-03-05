package client;

import client.ui.*;
import common.Message;
import common.PaintPoint;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


public class GuessPhase extends Phase {

    private final GameSession session;
    private final JPanel panel;
    private final JTextField guessField;

    private AwesomeText text1;
    private AwesomeText text2;

    /**
     * Constructor GuessPhase Client side.
     *
     * @param msg Message containing the image to guess on.
     */
    public GuessPhase(Message msg) {
        session = Game.getInstance().getSession();

        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(23, 0, 0, 0));

        AwesomeText guessMessage = new AwesomeText("What is this?");
        guessMessage.setTextColor(Color.RED);
        layout.setConstraintsRatioByWidth(guessMessage, .5f, .85f, .85f, 0.25f);
        AwesomeUtil.dynamicFont(guessMessage, 0.4f);
        panel.add(guessMessage);

        DrawPanel image= new DrawPanel((ArrayList<List<PaintPoint>>) msg.data.get("image"));
        panel.add(image);
        layout.setConstraintsRatioByWidth(image, .5f, .5f, .75f, 0.75f);

        guessField = new JTextField();
        panel.add(guessField);
        layout.setConstraintsRatioByWidth(guessField, .33f, .1f, .41f, 0.15f);

        AwesomeButton submit = new AwesomeButton("Guess!!", Assets.getMainmenuIcon(Assets.MENU_WHAM));
        AwesomeUtil.dynamicFont(submit, 0.4f);
        layout.setConstraintsRatioByWidth(submit, .73f, .1f, .3f, 0.35f);
        panel.add(submit);

        submit.addActionListener(e -> {
            if ( checkAndSendGuess(guessField.getText())) {
                this.text1 = new AwesomeText("Guess sent!! Waiting for others..");
                layout.setConstraintsRatioByWidth(this.text1, .5f, .5f, .8f, 0.25f);
                if (text2 != null) {
                    panel.remove(text2);
                }
                panel.remove(guessField);
                panel.remove(submit);
                panel.remove(image);
                panel.remove(guessMessage);
                panel.add(text1);
            } else {
                this.text2 = new AwesomeText("Only characters allowed a-z");
                layout.setConstraintsRatioByWidth(this.text2, .5f, .17f, .8f, 0.25f);
                panel.add(text2);
            }
        });

        PhaseUI phaseUI = session.getPhaseUI();
        phaseUI.setTitle("");
        phaseUI.startTimer((int) (session.getGameSettings().getGuessTimeMilliseconds() / 1000));
        phaseUI.setContent(panel);
    }


    /**
     * Helper function controls the input and sends the answer.
     * @param guessed
     * @return boolean
     */
    private boolean checkAndSendGuess(String guessed) {
        boolean sent = false;
        String str = guessed.trim();
        if (isAlphabetical(str) && !str.isEmpty()){
            Message submitMessage = new Message(Message.Type.SUBMIT_GUESS);
            submitMessage.addParameter("guess", str);
            Game.getInstance().sendMessage(submitMessage);
            sent = true;
        }
        return sent;
    }

    /**
     * Sends the entered guess back to the server.
     * @param guess
     */
    private void sendGuess(String guess) {
        if (!checkAndSendGuess(guess)) {
            Message submitMessage = new Message(Message.Type.SUBMIT_GUESS);
            submitMessage.addParameter("guess", "");
            Game.getInstance().sendMessage(submitMessage);
        }
    }


    /**
     * Controls so all characters in string are alphabetical
     * @param str
     * @return
     */
    private boolean isAlphabetical(String str){
        char[] temp = str.toCharArray();
        for ( int i = 0 ; i < temp.length; i++ ) {
            if ( temp[i] < 97 || temp[i] > 123 ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles messages recieved, TIMES UP triggers a submit.
     * @param msg
     */
    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.TIMES_UP) {
            sendGuess(guessField.getText());
        }
    }

}
