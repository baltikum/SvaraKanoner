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

    private final JPanel panel;
    private final JTextField guessField;
    private final Image wham;

    private AwesomeText text;

    private String guess;

    public GuessPhase(Message msg) {

        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(23, 0, 0, 0));

        BufferedImage tileMap = Assets.loadImage("mainmenu.png");
        wham = Assets.getTile(tileMap, 0, 0, 3, 1, 8);

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

        AwesomeButton submit = new AwesomeButton("Guess!!", wham);
        AwesomeUtil.dynamicFont(submit, 0.4f);
        layout.setConstraintsRatioByWidth(submit, .73f, .1f, .3f, 0.35f);
        panel.add(submit);

        text.setTextColor(Color.BLUE);
        layout.setConstraintsRatioByWidth(text, .5f, .17f, .8f, 0.25f);
        AwesomeUtil.dynamicFont(text, 0.25f);

        submit.addActionListener(e -> {
            if ( checkAndSendGuess(guessField.getText())) {
                layout.setConstraintsRatioByWidth(text, .5f, .5f, .8f, 0.25f);
                text = new AwesomeText("Guess sent!! Waiting for others..");
                panel.remove(guessField);
                panel.remove(submit);
                panel.remove(image);
            } else {
                text = new AwesomeText("Only characters allowed a-z");
            }
            panel.remove(text);
            panel.add(text);
        });

        PhaseUI phaseUI = Game.game.getPhaseUI();
        phaseUI.setTitle("");
        phaseUI.startTimer((int) (Game.game.getGameSettings().getGuessTimeMilliseconds() / 1000));
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
        str = str.toLowerCase(Locale.ROOT);

        if ( isAlphabetical(str)){
            System.out.println(str);
            Message submitMessage = new Message(Message.Type.SUBMIT_GUESS);
            submitMessage.addParameter("guess", str );
            Game.game.sendMessage(submitMessage);
            sent = true;
        }
        return sent;
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


    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.TIMES_UP) {
            checkAndSendGuess(guessField.getText());
        }
    }
}
