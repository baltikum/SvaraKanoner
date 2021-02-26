package server;

import common.GameSettings;
import common.Message;
import common.Pair;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class GuessPhase extends Phase {

    private GameSession gameSession;
    private RoundData roundData;
    private GameSettings settings;
    private HashMap<Integer,Image> guessImages;
    private int submits;


    public GuessPhase(GameSession session){
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.guessImages = this.roundData.getImagesToGuessOn();
        this.settings = this.gameSession.getGameSettings();
        this.submits = 0;

        timeLeft = new Timer((int) settings.getGuessTimeMilliseconds(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });




        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.IMAGE_DATA);
      //      message.addParameter("image", guessImages.get(client.getId())); // Nån annan egen klass som 'r serializable
            client.sendMessage(message);
        }


    }

    /**
     * Sends messsage to all clients.
     * @param msg
     */
    private void phaseMessage(Message msg){
        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            client.sendMessage(msg);
        }
    }

    /**
     * Message handling of this phase, server side.
     * @param msg Message
     */
    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SUBMIT_GUESS-> {
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
            case IMAGE_DATA_RECEIVED -> {
                System.out.println("Image data received at clients side"); // Response ?
            }
        }

    }
}
