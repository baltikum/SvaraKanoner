package server;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import common.GameSettings;
import common.Message;
import common.Phase;
import server.ClientHandler;
import server.GameSession;
import server.RoundData;

public class DrawPhase extends Phase {

    private GameSession gameSession;
    private RoundData roundData;
    private int submits;

    public DrawPhase(GameSession session){
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.submits = 0;

        timeLeft = new Timer((int) settings.getGuessTimeMilliseconds(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.WORD_DATA);
            message.addParameter("words", generatedWords.get(client.getId()));
            client.sendMessage(message);
        }


    }





    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SUBMIT_PICTURE-> {	    //  Hur göra här med bild, ska alltid gå till guessPhase
                // kolla medelandet
                gameSession.getCurrentRoundData().saveGuess(msg.player.getId(), (String) msg.data.get("guess"));
                this.submits++;
                if ( submits == roundData.getNumberOfWords() ) {
                    if ( roundData.getRoundPartCount() == roundData.getNumberOfWords()) {
                        phaseMessage(new Message(Message.Type.GOTO_REVEAL_PHASE));
                        //             gameSession.setPhase(new RevealPhase());
                    } else {
                        phaseMessage(new Message(Message.Type.GOTO_DRAW_PHASE));
                        //                   gameSession.setPhase(new DrawPhase());
                    }
                }
            }
            case WORD_DATA_RECEIVED -> {
                System.out.println("Word data received at clients side"); // Response ?
            }
        }

    }



}
