package server;

import common.Message;
import common.Phase;

/**
 *
 */
public class RevealPhase extends Phase {

    private final GameSession session;
    private final RoundData round;
    private WordTracker tracker = null;
    private int currentWordIndex = 0;
    private int currentRevealIndex = 0;
    private boolean shouldRevealDrawing = true;
    private final boolean keepScores;

    public RevealPhase(GameSession session) {
        this.session = session;
        this.round = session.getCurrentRoundData();
        keepScores = session.getGameSettings().getKeepScore();

        Message gotoRevealPhase = new Message(Message.Type.GOTO);
        gotoRevealPhase.addParameter("phase", "RevealPhase");
        fillRevealWordMessage(gotoRevealPhase);
        session.sendMessageToAll(gotoRevealPhase);
    }

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
