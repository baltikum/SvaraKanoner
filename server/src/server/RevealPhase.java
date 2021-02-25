package server;

import common.Message;
import common.Phase;

import java.util.List;


public class RevealPhase extends Phase {

    private final GameSession session;
    private final RoundData round;
    private WordTracker tracker = null;
    private int currentWordIndex = 0;
    private int currentRevealIndex = 0;

    public RevealPhase(GameSession session) {
        List<RoundData> sessionRounds = session.getSessionRounds();
        this.session = session;
        this.round = sessionRounds.get(sessionRounds.size() - 1);
        revealNext();
    }

    public void revealNext() {
        if (round.getNumberOfWords() == currentWordIndex) {
            // TODO: Go to win phase.
        } else {
            if (tracker == null){
                String word = round.getRoundWords().get(currentWordIndex);
                tracker = round.getWordTracker(word);
                ClientHandler wordOwner = session.getConnectedPlayer(tracker.getWordOwnerId());


            } else if (currentRevealIndex % 2 == 0) {
                // TODO: Reveal drawing
            } else {
                // TODO: Reveal guess
            }
        }
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        }
    }

}
