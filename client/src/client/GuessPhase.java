package client;

import client.ui.*;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GuessPhase extends Phase {

    private final JPanel panel;

    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();


    private Image imageToGuess;
    private String guess;

    public GuessPhase() {

        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
       // panel.setOpaque(true);
        panel.setBackground(new Color(23, 0, 0, 0));

        AwesomeImage image = new AwesomeImage(imageToGuess);

        Message submitMessage = new Message(Message.Type.SUBMIT_GUESS);

        JTextField guessField = new JTextField();
        panel.add(guessField);
        layout.setConstraintsRatioByWidth(guessField, .75f, .167f * 5, .3f, 0.25f);

        AwesomeButton submit = new AwesomeButton("Guess");
        panel.add(submit);
        layout.setConstraintsRatioByWidth(submit, .75f, .167f * 5, .3f, 0.25f);

        submit.addActionListener(e -> {
            submitMessage.addParameter("guess", guessField.getText());
            Game.game.sendMessage(submitMessage);
        });
        AwesomeUtil.dynamicFont(submit, 1.0f);
        Game.game.setContentPanel(panel);
    }

   // Game.game.setCurrentPhase(new PickWordPhase());

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case IMAGE_DATA -> {
                this.imageToGuess = (Image) msg.data.get("image");
                Game.game.sendMessage(new Message(Message.Type.IMAGE_DATA_RECEIVED));
            }
        }
    }
}
