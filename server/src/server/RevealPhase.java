package server;

import common.Message;
import common.Phase;

import java.io.Serializable;


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

    public RevealPhase(GameSession session) {
        this.session = session;
        this.round = session.getCurrentRoundData();
        revealNext();
    }

    public void revealNext() {
        Message msg = new Message(Message.Type.REVEAL_NEXT);
        if (currentWordIndex < round.getNumberOfWords()) {
            int playerId = -1;
            if (tracker == null){
                String word = round.getRoundWords().get(currentWordIndex);
                tracker = round.getWordTracker(word);
                playerId = tracker.getWordOwnerId();
                msg.addParameter("word", tracker.getWord());
            } else {
                if (shouldRevealDrawing) {
                    Pair img = tracker.getDrawing(currentRevealIndex);
                    // msg.addParameter("drawing", img.getImage());
                    playerId = img.getPlayerId();
                } else {
                    Pair guess = tracker.getGuess(currentRevealIndex);
                    msg.addParameter("guess", guess.getGuess());
                    if (++currentRevealIndex == tracker.getAllGuesses().size()) {
                        ++currentWordIndex;
                        tracker = null;
                    }
                    playerId = guess.getPlayerId();
                }
                shouldRevealDrawing = !shouldRevealDrawing;
            }
            msg.addParameter("playerId", playerId);
        } else {
            if (session.getSessionRounds().size() == session.getGameSettings().getNumRounds()) {
                msg.addParameter("goto", "WinnerPhase");
            } else {
                msg.addParameter("goto", "PickWordPhase");
            }
        }
        session.sendMessageToAll(msg);
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        }
    }

}
