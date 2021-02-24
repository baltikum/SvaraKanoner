package server;

import common.Message;
import common.Phase;

import java.util.List;

public class GuessPhase extends Phase {

    private GameSession gameSession;

    public GuessPhase(GameSession session){
        this.gameSession = session;
    }

    @Override
    public void message(Message msg) {

    }
}
