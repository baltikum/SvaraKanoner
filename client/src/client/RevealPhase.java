package client;

import client.ui.*;
import common.Message;
import common.PaintPoint;
import common.Phase;
import common.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class RevealPhase extends Phase {
    private final GameSession session;

    private final AwesomeIconLabel drawingOwnerLabel = new AwesomeIconLabel(null, "DREW");
    private final AwesomeIconLabel guessOwnerLabel = new AwesomeIconLabel(null, "GUESSED");
    private final AwesomeText guessComp = new AwesomeText("");
    private final DrawPanel drawingComp = new DrawPanel(null);

    public RevealPhase(Message gotoMessage) {
        session = Game.getInstance().getSession();

        drawingOwnerLabel.setVisible(false);
        guessOwnerLabel.setVisible(false);
        guessComp.setVisible(false);
        drawingComp.setVisible(false);

        // The incredible next btn
        AwesomeButton nextBtn = new AwesomeButton("NEXT");
        nextBtn.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        nextBtn.addActionListener( e -> next() );
        AwesomeUtil.wiggleOnHover(nextBtn, 30.0f);

        // Dynamic fonts
        AwesomeUtil.dynamicFont(drawingOwnerLabel, 1.0f);
        AwesomeUtil.dynamicFont(guessOwnerLabel, 1.0f);
        AwesomeUtil.dynamicFont(guessComp, 1.0f);
        AwesomeUtil.dynamicFont(nextBtn, 1.0f);

        // Reveal panel
        PercentLayout revealLayout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(revealLayout);
        panel.setBackground(new Color(0xe67e22));
        panel.add(drawingOwnerLabel);
        panel.add(guessOwnerLabel);
        panel.add(guessComp);
        panel.add(drawingComp);
        panel.add(nextBtn);
        revealLayout.setConstraintsRatioByWidth(drawingOwnerLabel, 0.25f, 0.3f, 0.4f, .2f);
        revealLayout.setConstraintsRatioByWidth(drawingComp, 0.25f, 0.6f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(guessOwnerLabel, 0.75f, 0.3f, 0.4f, .2f);
        revealLayout.setConstraintsRatioByWidth(guessComp, 0.75f, 0.6f, 0.4f, .4f);
        revealLayout.setConstraintsRatioByWidth(nextBtn, 0.5f, 0.9f, 0.4f, .25f);

        revealNext(gotoMessage);

        PhaseUI phaseUI = session.getPhaseUI();
        phaseUI.hideTimer();
        phaseUI.setContent(panel);
    }

    public void next() {
        Game.getInstance().sendMessage(new Message(Message.Type.REVEAL_NEXT_REQUEST));
    }

    public void revealNext(Message msg) {
        Player player = session.getPlayerById((int) msg.data.get("playerId"));
        if (msg.data.containsKey("drawing")) {
            revealNextDrawing((ArrayList<List<PaintPoint>>) msg.data.get("drawing"), player);
        } else if (msg.data.containsKey("guess")) {
            revealNextGuess((String) msg.data.get("guess"), player);
        } else if (msg.data.containsKey("word")) {
            revealNextWord((String) msg.data.get("word"));
        }
    }

    public void revealNextWord(String word) {
        session.getPhaseUI().setTitle(word);
        drawingOwnerLabel.setVisible(false);
        drawingComp.setVisible(false);
        guessOwnerLabel.setVisible(false);
        guessComp.setVisible(false);
    }

    public void revealNextDrawing(ArrayList<List<PaintPoint>> drawing, Player player) {
        Image playerIcon = Assets.getPlayerIcons()[player.getAvatarId()];
        drawingComp.setDrawData(drawing);
        drawingOwnerLabel.setIcon(playerIcon);
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

        effectBuilder
                .addScaleKey(0.0f, 0.0f, 0)
                .addScaleKey(1.0f, 1.0f, 500)
                .addRotationKey(360.0f, 500);
        ownerComp.setVisible(true);
        revealComp.setVisible(true);

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

