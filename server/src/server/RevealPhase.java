package server;

import common.Message;
import common.Phase;
import common.Pair;

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
                Pair pair = shouldRevealDrawing ? tracker.getDrawing(currentRevealIndex) :
                                                  tracker.getGuess(currentRevealIndex);
                revealNextMsg.addParameter("playerId", pair.getPlayerId());
                if (shouldRevealDrawing) {
                    revealNextMsg.addParameter("drawing", pair.getImage());
                } else {
                    revealNextMsg.addParameter("guess", pair.getGuess());
                    if (++currentRevealIndex == tracker.getAllGuesses().size()) {
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
        String word = round.getRoundWords().get(currentWordIndex);
        tracker = round.getWordTracker(word);
        msg.addParameter("word", word);
        msg.addParameter("playerId", tracker.getWordOwnerId());
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        }
    }

}
