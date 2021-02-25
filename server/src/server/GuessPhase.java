package server;

import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GuessPhase extends Phase {

    private GameSession gameSession;

    public GuessPhase(GameSession session){
        this.gameSession = session;



        timeLeft = new Timer((int) gameSession.getGameSettings().getGuessTimeMilliseconds(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    @Override
    public void message(Message msg) {

    }
}
