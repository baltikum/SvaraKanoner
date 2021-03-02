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

    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();
    private final Image wham;


    private ArrayList<List<PaintPoint>> imageToGuess;
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

        DrawPanel image= new DrawPanel(imageToGuess);
        panel.add(image);
        layout.setConstraintsRatioByWidth(image, .5f, .5f, .75f, 0.75f);

        JTextField guessField = new JTextField();
        panel.add(guessField);
        layout.setConstraintsRatioByWidth(guessField, .33f, .1f, .41f, 0.15f);

        AwesomeButton submit = new AwesomeButton("Guess!!", wham);
        AwesomeUtil.dynamicFont(submit, 0.4f);
        layout.setConstraintsRatioByWidth(submit, .73f, .1f, .3f, 0.35f);
        panel.add(submit);

        submit.addActionListener(e -> {
            AwesomeText text;
            if ( checkAndSendGuess(guessField.getText())) {
                text = new AwesomeText("Guess sent!! Waiting for others..");
            } else {
                text = new AwesomeText("Only characters allowed a-z");
            }
            AwesomeUtil.dynamicFont(text, 0.25f);
            text.setTextColor(Color.BLUE);
            layout.setConstraintsRatioByWidth(text, .5f, .17f, .8f, 0.25f);
            panel.remove(text);
            panel.add(text);
        });
        Game.game.setContentPanel(panel);
    }


    /**
     * Helper function controls the input and sends the answer.
     * @param guessed
     * @return boolean
     */
    private boolean checkAndSendGuess(String guessed ){
        if ( guessed.equals("") ) {
            return false;
        }
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
    }
}
