package client;

import client.ui.*;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;


public class RevealPhase extends Phase {
    private final AwesomeIconLabel currentWordOwner = new AwesomeIconLabel(null, null);
    private final AwesomeText currentWordText = new AwesomeText("");
    private final AwesomeText revealedText = new AwesomeText("");
    private final AwesomeImage revealedDrawing = new AwesomeImage(null);

    public RevealPhase() {
        JPanel whomContainerPanel = new JPanel();
        whomContainerPanel.setLayout(new BoxLayout(whomContainerPanel, BoxLayout.X_AXIS));
        JPanel whomPanel = new JPanel();
        whomPanel.add(currentWordOwner);
        whomPanel.add(currentWordText);
        whomContainerPanel.add(whomPanel);

        PercentLayout layout = new PercentLayout(.8f);
        JPanel revealPanel = new JPanel(layout);
        revealPanel.add(revealedDrawing);
        revealPanel.add(revealedText);

        AwesomeButton next = new AwesomeButton("next");
        next.addActionListener(e -> next());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(whomPanel, BorderLayout.PAGE_START);
        panel.add(whomPanel, BorderLayout.CENTER);
        Game.game.setContentPanel(panel);
    }

    public void next() {
        Message msg = new Message(Message.Type.REVEAL_NEXT_REQUEST);
        Game.game.sendMessage(msg);
    }

    public void revealNext(Message msg) {

    }

    public void revealNextWord() {

    }

    public void revealNextDrawing() {

    }

    public void revealNextGuess() {

    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT) {
            revealNext(msg);
        }
    }

}

