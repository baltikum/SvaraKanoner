package server;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import common.GameSettings;
import common.Message;
import common.PaintPoint;
import common.Phase;
import server.ClientHandler;
import server.GameSession;
import server.RoundData;

public class DrawPhase extends Phase {

    private GameSession gameSession;
    private RoundData roundData;
    private int submits;
    private GameSettings gameSettings;

    public DrawPhase(GameSession session){
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.submits = 0;
        this.gameSettings = gameSession.getGameSettings();

        timeLeft = new Timer((int) gameSettings.getGuessTimeMilliseconds(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.WORD_DATA);
            message.addParameter("word", roundData.getWordsToDraw().get(client.getId()));
            client.sendMessage(message);
        }


    }





    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SUBMIT_PICTURE-> {	    //  Hur göra här med bild, ska alltid gå till guessPhase
                // kolla medelandet
                gameSession.getCurrentRoundData().saveImage(msg.player.getId(), (String) msg.data.get("guess"), (PaintPoint) msg.data.get("image"));
                this.submits++;
                if ( submits == gameSession.getConnectedPlayers().size() ) {

                        gameSession.sendMessageToAll(new Message(Message.Type.GOTO_GUESS_PHASE));
                                    gameSession.setPhase(new GuessPhase(gameSession));
                }
            }
            case WORD_DATA_RECEIVED -> {
                System.out.println("Word data received at clients side"); // Response ?
            }
        }

    }



}
