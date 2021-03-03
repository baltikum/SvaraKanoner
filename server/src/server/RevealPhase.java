package server;

import common.Message;
import common.Phase;

/**
 * Takes care of the server side responsibilities for the RevealPhase.
 * Comes after the guess phase.
 *
 * @author Jesper Jansson
 * @version 04/03/21
 */
public class RevealPhase extends Phase {

    private final GameSession session;
    private final RoundData round;
    private WordTracker tracker = null;
    private int currentWordIndex = 0;
    private int currentRevealIndex = 0;
    private boolean shouldRevealDrawing = true;
    private final boolean keepScores;

    /**
     * Constructs a reveal phase and tells all clients to goto it.
     * @param session The game session that should go to reveal phase.
     */
    public RevealPhase(GameSession session) {
        this.session = session;
        this.round = session.getCurrentRoundData();
        keepScores = session.getGameSettings().getKeepScore();

        Message gotoRevealPhase = new Message(Message.Type.GOTO);
        gotoRevealPhase.addParameter("phase", "RevealPhase");
        fillRevealWordMessage(gotoRevealPhase);
        session.sendMessageToAll(gotoRevealPhase);
    }

    /**
     * Reveals the next in order word/drawing/guess and sends it to all the clients and lastly changes phase.
     * For example 2 players will result in word, drawing, guess, word, drawing, guess and then pick word or winner phase.
     */
    public void revealNext() {
        if (currentWordIndex < round.getNumberOfWords()) {
            Message revealNextMsg = new Message(Message.Type.REVEAL_NEXT);
            if (tracker == null){
                fillRevealWordMessage(revealNextMsg);
            } else {
                WordTracker.Entry entry = tracker.getEntry(currentRevealIndex);
                if (shouldRevealDrawing) {
                    revealNextMsg.addParameter("playerId", entry.getImageSubmitterId());
                    revealNextMsg.addParameter("drawing", entry.getImage());
                } else {
                    revealNextMsg.addParameter("playerId", entry.getGuessSubmitterId());
                    revealNextMsg.addParameter("imagePlayerId", entry.getImageSubmitterId());
                    revealNextMsg.addParameter("guess", entry.getGuess());
                    revealNextMsg.addParameter("receivesPoints", keepScores && entry.isCorrect());
                    if (entry == tracker.getLatestEntry()) {
                        ++currentWordIndex;
                        currentRevealIndex = 0;
                        tracker = null;
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

    /**
     * Fills out the parameters of the current word reveal message.
     * @param msg The message to fill out.
     */
    private void fillRevealWordMessage(Message msg) {
        tracker = round.getWordTracker(currentWordIndex);
        msg.addParameter("word", tracker.getPickedWord());
        msg.addParameter("playerId", tracker.getPickerId());
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        }
    }

}
