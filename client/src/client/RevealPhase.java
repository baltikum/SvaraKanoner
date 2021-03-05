package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Takes care of the client side responsibilities during the reveal phase.
 *
 * Waits until a REVEAL_NEXT message and then reveals the appropriate word/drawing/guess.
 *
 * @author Jesper Jansson
 * @version 04/03/21
 */
public class RevealPhase implements Phase {
    private final GameSession session;

    private final AwesomeIconLabel drawingOwnerLabel = new AwesomeIconLabel(null, "DREW");
    private final AwesomeIconLabel guessOwnerLabel = new AwesomeIconLabel(null, "GUESSED");
    private final AwesomeText guessComp = new AwesomeText("");
    private final DrawPanel drawingComp = new DrawPanel(null);
    private final AwesomeButton objectBtn = new AwesomeButton("Objection!", Assets.getMainmenuIcon(Assets.MENU_WHAM));
    private final AwesomeText objectionCountComp = new AwesomeText("");
    private int objectionCount = 0;

    /**
     *
     *
     * @param gotoMessage GOTO message with the appropriate data for revealing the first word.
     */
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
        panel.add(objectBtn);
        panel.add(objectionCountComp);
        panel.add(nextBtn);
        revealLayout.setConstraintsRatioByWidth(drawingOwnerLabel, 0.25f, 0.3f, 0.4f, .2f);
        revealLayout.setConstraintsRatioByWidth(drawingComp, 0.25f, 0.6f, 0.4f, 1.0f);
        revealLayout.setConstraintsRatioByWidth(guessOwnerLabel, 0.75f, 0.3f, 0.4f, .2f);
        revealLayout.setConstraintsRatioByWidth(guessComp, 0.75f, 0.6f, 0.4f, .4f);
        revealLayout.setConstraintsRatioByWidth(objectBtn, 0.6f, 0.75f, 0.3f, 0.33f);
        revealLayout.setConstraintsRatioByWidth(objectionCountComp, 0.85f, 0.75f, 0.3f, 0.33f);
        revealLayout.setConstraintsRatioByWidth(nextBtn, 0.5f, 0.9f, 0.4f, .25f);

        objectBtn.addActionListener( e -> {
            objectBtn.setVisible(false);
            Game.getInstance().sendMessage(new Message(Message.Type.OBJECT));
        } );
        objectBtn.setVisible(false);

        revealNext(gotoMessage);

        PhaseUI phaseUI = session.getPhaseUI();
        phaseUI.hideTimer();
        phaseUI.setContent(panel);
    }

    /**
     * Tells the server to go to the next word/drawing/guess.
     */
    public void next() {
        Game.getInstance().sendMessage(new Message(Message.Type.REVEAL_NEXT_REQUEST));
    }

    /**
     * @param msg A message containing what to reveal.
     */
    public void revealNext(Message msg) {
        Player player = session.getPlayerById((int) msg.data.get("playerId"));
        if (msg.data.containsKey("drawing")) {
            revealNextDrawing((ArrayList<List<PaintPoint>>) msg.data.get("drawing"), player);
        } else if (msg.data.containsKey("guess")) {
            Player drawingPlayer = session.getPlayerById((int) msg.data.get("imagePlayerId"));
            boolean receivesPoints = (boolean) msg.data.get("receivesPoints");
            boolean canObject = (boolean) msg.data.get("canObject");

            revealNextGuess((String) msg.data.get("guess"), player, drawingPlayer, receivesPoints, canObject);
        } else if (msg.data.containsKey("word")) {
            revealNextWord((String) msg.data.get("word"));
        }
    }

    /**
     * @param word The word to reveal
     */
    public void revealNextWord(String word) {
        session.getPhaseUI().setTitle(word);
        drawingOwnerLabel.setVisible(false);
        drawingComp.setVisible(false);
        guessOwnerLabel.setVisible(false);
        guessComp.setVisible(false);
        objectBtn.setVisible(false);
        objectionCountComp.setText("");
        objectionCount = 0;

        session.getPhaseUI().resetPlayerColors();
    }

    /**
     * @param drawing The drawing to reveal.
     * @param player The player who drew it.
     */
    public void revealNextDrawing(ArrayList<List<PaintPoint>> drawing, Player player) {
        Image playerIcon = Assets.getAvatarImage(player.getAvatarId());
        drawingComp.setDrawData(drawing);
        drawingOwnerLabel.setIcon(playerIcon);
        reveal(drawingComp, drawingOwnerLabel);
    }

    /**
     * @param guess The guess to reveal.
     * @param player The player who guessed the word.
     * @param imagePlayerId The id of the player who drew the accompanying drawing.
     * @param receivesPoints True if points were received else false.
     * @param canObject True if the object button should appear.
     */
    public void revealNextGuess(String guess, Player player, Player imagePlayerId, boolean receivesPoints, boolean canObject) {
        Image playerIcon = Assets.getAvatarImage(player.getAvatarId());
        guessComp.setText(guess);
        guessOwnerLabel.setIcon(playerIcon);
        reveal(guessComp, guessOwnerLabel);

        if (receivesPoints) {
            session.getPhaseUI().setColorOfPlayer(player.getId(), Color.GREEN);
            session.getPhaseUI().setColorOfPlayer(imagePlayerId.getId(), Color.GREEN);
        } else if (canObject) {
            objectBtn.setVisible(true);
        }
    }

    /**
     * Helper function for revealing, makes the components visible and animates them.
     * @param ownerComp The owner label to show and animate.
     * @param revealComp The reveal component to show and animate.
     */
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

    private void someoneObjected(Message msg) {
        AwesomeEffect.create()
                .addRotationKey(45.0f, 200)
                .addRotationKey(-45.0f, 600)
                .addRotationKey(0.0f, 800)
                .animate(objectionCountComp);

        boolean success = (boolean) msg.data.get("success");
        if (success) {
            int playerId0 = (int) msg.data.get("player0");
            int playerId1 = (int) msg.data.get("player1");
            session.getPhaseUI().setColorOfPlayer(playerId0, Color.GREEN);
            session.getPhaseUI().setColorOfPlayer(playerId1, Color.GREEN);
            objectionCountComp.setText("It is correct!");
        } else {
            objectionCountComp.setText(String.valueOf(++objectionCount));
        }
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT) {
            revealNext(msg);
        } else if (msg.type == Message.Type.SOMEONE_OBJECTED) {
            someoneObjected(msg);
        }
    }

}

