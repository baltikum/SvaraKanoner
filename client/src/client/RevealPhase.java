package client;

import client.ui.*;
import common.Message;
import common.Phase;
import common.Player;

import javax.swing.*;
import java.awt.*;


public class RevealPhase extends Phase {
    private final AwesomeIconLabel currentWordOwner = new AwesomeIconLabel(null, null);

    private final AwesomeIconLabel drawingOwnerLabel = new AwesomeIconLabel(null, "drew");
    private final AwesomeIconLabel guessOwnerLabel = new AwesomeIconLabel(null, "guessed");
    private final AwesomeText guessComp = new AwesomeText("");
    private final AwesomeImage drawingComp = new AwesomeImage(null);

    public RevealPhase(Message gotoMessage) {
        currentWordOwner.setVisible(false);
        drawingOwnerLabel.setVisible(false);
        guessOwnerLabel.setVisible(false);
        guessComp.setVisible(false);
        drawingComp.setVisible(false);

        // Upper panel
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
        upperPanel.add(currentWordOwner);
        currentWordOwner.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // The incredible next btn
        AwesomeButton nextBtn = new AwesomeButton("next");
        nextBtn.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        nextBtn.addActionListener( e -> next() );
        AwesomeUtil.dynamicFont(nextBtn, 1.0f);
        AwesomeUtil.wiggleOnHover(nextBtn, 30.0f);

        // Reveal panel
        PercentLayout revealLayout = new PercentLayout(1.0f);
        JPanel revealPanel = new JPanel(revealLayout);
        revealPanel.add(drawingOwnerLabel);
        revealPanel.add(guessOwnerLabel);
        revealPanel.add(guessComp);
        revealPanel.add(drawingComp);
        revealPanel.add(nextBtn);
        revealLayout.setConstraintsRatioByWidth(drawingOwnerLabel, 0.25f, 0.1f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(drawingComp, 0.25f, 0.6f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(guessOwnerLabel, 0.75f, 0.1f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(guessComp, 0.75f, 0.6f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(nextBtn, 0.5f, 0.9f, 0.4f, .25f);

        // Outer panel
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(upperPanel, BorderLayout.NORTH);
        outerPanel.add(revealPanel, BorderLayout.CENTER);
        Game.game.setContentPanel(outerPanel);

        outerPanel.setBackground(new Color(0xe67e22));
        upperPanel.setBackground(new Color(0xe67e22));
        revealPanel.setBackground(new Color(0xe67e22));

        revealNext(gotoMessage);
    }

    public void next() {
        Game.game.sendMessage(new Message(Message.Type.REVEAL_NEXT_REQUEST));
    }

    public void revealNext(Message msg) {
        Player player = Game.game.getPlayer((int) msg.data.get("playerId"));
        if (msg.data.containsKey("drawing")) {
            revealNextDrawing((Image) msg.data.get("drawing"), player);
        } else if (msg.data.containsKey("guess")) {
            revealNextGuess((String) msg.data.get("guess"), player);
        } else if (msg.data.containsKey("word")) {
            revealNextWord((String) msg.data.get("word"), player);
        }
    }

    public void revealNextWord(String word, Player player) {
        Image playerIcon = Assets.getPlayerIcons()[player.getAvatarId()];
        currentWordOwner.setVisible(true);
        currentWordOwner.setIcon(playerIcon);
        currentWordOwner.setText(player.getName() + " picked the word " + word);
    }

    public void revealNextDrawing(Image drawing, Player player) {
        Image playerIcon = Assets.getPlayerIcons()[player.getAvatarId()];
        drawingComp.setImage(drawing);
        guessOwnerLabel.setIcon(playerIcon);
        reveal(drawingComp, drawingOwnerLabel);
    }

    public void revealNextGuess(String guess, Player player) {
        Image playerIcon = Assets.getPlayerIcons()[player.getAvatarId()];
        guessComp.setText(guess);
        guessOwnerLabel.setIcon(playerIcon);
        reveal(guessComp, guessOwnerLabel);
    }

    private void reveal(JComponent ownerComp, JComponent revealComp) {
        AwesomeEffect.Builder effectBuilder = AwesomeEffect.create();

        if (ownerComp.isVisible()) {
            effectBuilder
                    .addScaleKey(1.0f, 1.0f, 500)
                    .addScaleKey(0.0f, 0.0f, 1000)
                    .addRotationKey(-360.0f, 500)
                    .addRotationKey(0.0f, 1000);
        } else {
            effectBuilder
                    .addScaleKey(0.0f, 0.0f, 0)
                    .addScaleKey(1.0f, 1.0f, 500)
                    .addRotationKey(360.0f, 500);
            ownerComp.setVisible(true);
            revealComp.setVisible(true);
        }

        effectBuilder.animate((AwesomeEffect.User) ownerComp, AwesomeEffect.COMPONENT);
        effectBuilder.animate((AwesomeEffect.User) revealComp, AwesomeEffect.COMPONENT);
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT) {
            revealNext(msg);
        }
    }

}

