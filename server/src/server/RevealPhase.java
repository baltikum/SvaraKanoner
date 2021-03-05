package server;

import common.*;
import java.util.ArrayList;

/**
 * Takes care of the server side responsibilities for the RevealPhase.
 * Comes after the guess phase.
 *
 * @author Jesper Jansson
 * @version 04/03/21
 */
public class RevealPhase implements Phase {

    private final GameSession session;
    private final RoundData round;

    private boolean shouldRevealDrawing = true;
    private int currentWordIndex = 0;
    private int currentEntryIndex = 0;
    private WordTracker currentWordTracker;
    private WordTracker.Entry currentEntry;

    private final boolean keepScores;
    private final ArrayList<Integer> objectingPlayerIds;
    private boolean canObject = true;
    private boolean objectionHappend = false;
    private WordTracker.Entry objectionTarget = null;

    /**
     * Constructs a reveal phase and tells all clients to goto it.
     * @param session The game session that should go to reveal phase.
     */
    public RevealPhase(GameSession session) {
        this.session = session;
        this.round = session.getCurrentRoundData();
        keepScores = session.getGameSettings().getKeepScore();
        objectingPlayerIds = new ArrayList<>(session.getConnectedPlayers().size());
        canObject = keepScores;

        currentWordTracker = round.getWordTracker(currentWordIndex++);
        currentEntry = currentWordTracker.getEntry(currentEntryIndex++);

        Message gotoRevealPhase = new Message(Message.Type.GOTO);
        gotoRevealPhase.addParameter("phase", "RevealPhase");
        gotoRevealPhase.addParameter("word", currentWordTracker.getPickedWord());
        gotoRevealPhase.addParameter("playerId", currentWordTracker.getPickerId());
        session.sendMessageToAll(gotoRevealPhase);
    }

    /**
     * Reveals the next in order word/drawing/guess and sends it to all the clients and lastly changes phase.
     * For example 2 players will result in word, drawing, guess, word, drawing, guess and then pick word or winner phase.
     */
    public void revealNext() {
        // Reset objection target
        objectionTarget = null;

        if (currentWordTracker != null) {
            Message revealNextMsg = new Message(Message.Type.REVEAL_NEXT);

            if (currentEntry == null){
                revealNextMsg.addParameter("word", currentWordTracker.getPickedWord());
                revealNextMsg.addParameter("playerId", currentWordTracker.getPickerId());
                currentEntryIndex = 0;
                currentEntry = currentWordTracker.getEntry(currentEntryIndex++); // Goto the first entry next.
            } else {
                if (shouldRevealDrawing) {
                    revealNextMsg.addParameter("playerId", currentEntry.getImageSubmitterId());
                    revealNextMsg.addParameter("drawing", currentEntry.getImage());
                } else {
                    // Players can only object if everyone before received points
                    if (!currentEntry.isCorrect() && !objectionHappend && currentEntryIndex > 1) {
                        canObject = false;
                    } else if (canObject) {
                        objectionTarget = currentEntry;
                    }
                    // Reset objection
                    objectingPlayerIds.clear();
                    objectionHappend = false;

                    revealNextMsg.addParameter("playerId", currentEntry.getGuessSubmitterId());
                    revealNextMsg.addParameter("imagePlayerId", currentEntry.getImageSubmitterId());
                    revealNextMsg.addParameter("guess", currentEntry.getGuess());
                    revealNextMsg.addParameter("receivesPoints", keepScores && currentEntry.isCorrect());
                    revealNextMsg.addParameter("canObject", canObject);

                    // If last entry advance to the next word or phase next.
                    if (currentEntry == currentWordTracker.getLatestEntry()) {
                        canObject = keepScores;
                        currentEntry = null;
                        if (currentWordIndex < round.getNumberOfWords())
                            currentWordTracker = round.getWordTracker(currentWordIndex++); // Goto the next word next,
                        else
                            currentWordTracker = null; // Goto the next phase next.
                    } else {
                        currentEntry = currentWordTracker.getEntry(currentEntryIndex++); // Goto the next entry next.
                    }
                }
                shouldRevealDrawing = !shouldRevealDrawing;
            }
            session.sendMessageToAll(revealNextMsg);
        } else {
            if (session.getSessionRounds().size() == session.getGameSettings().getNumRounds())
                session.setPhase(new WinnerPhase(session));
            else
                session.setPhase(new PickWordPhase(session));
        }
    }

    private void object(Player player) {
        // If we have not lastly revealed a guess or the players cant object then do nothing.
        if (objectionTarget == null || objectionTarget.isCorrect()) return;

        // Check if the player has already objected do nothing.
        int index = objectingPlayerIds.indexOf(player.getId());
        if (index != -1) return;

        // Add the player to players wo objected and check if all have objected.
        objectingPlayerIds.add(player.getId());
        boolean success = objectingPlayerIds.size() == session.getConnectedPlayers().size();

        Message msg = new Message(Message.Type.SOMEONE_OBJECTED);
        msg.addParameter("success", success);
        if (success) {
            objectionHappend = true;
            msg.addParameter("player0", objectionTarget.getImageSubmitterId());
            msg.addParameter("player1", objectionTarget.getGuessSubmitterId());
            session.getConnectedPlayer(objectionTarget.getImageSubmitterId()).givePoints(1);
            session.getConnectedPlayer(objectionTarget.getGuessSubmitterId()).givePoints(1);
        }
        session.sendMessageToAll(msg);
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        } else if (msg.type == Message.Type.OBJECT) {
            object(msg.player);
        }
    }

}
