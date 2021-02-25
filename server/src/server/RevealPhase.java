package server;

import common.Message;
import common.Phase;

import java.util.List;


public class RevealPhase extends Phase {
    private final GameSession session;
    private int currentRoundIndex = 0;
    private int currentWordIndex = 0;
    private int currentRevealIndex = -1;

    public RevealPhase(GameSession session) {
        this.session = session;
        revealNext();
    }

    public void revealNext() {
        List<RoundData> sessionRounds = session.getSessionRounds();
        if (sessionRounds.size() == currentRoundIndex) {
            // TODO: Go to win phase.
        } else if (currentRevealIndex == -1){
            // TODO: Reveal given word
        } else if (currentRevealIndex % 2 == 0) {
            // TODO: Reveal drawing
        } else {
            // TODO: Reveal guess
        }
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.REVEAL_NEXT_REQUEST) {
            revealNext();;
        }
    }

}
