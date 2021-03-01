package server;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class GuessPhase extends Phase {

    private GameSession gameSession;
    private RoundData roundData;
    private GameSettings settings;
    private HashMap<Integer, PaintPoint> guessImages;
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
                advancePhase();
            }
        });

        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message;
            if ( guessImages.containsKey(client.getId())) {
                message = new Message(Message.Type.GOTO_GUESS_PHASE);
            } else {
                message = new Message(Message.Type.GOTO_WAIT_PHASE);
            }
            client.sendMessage(message);
        }


        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.IMAGE_DATA);
      //      message.addParameter("image", guessImages.get(client.getId())); // Nån annan egen klass som är serializable
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

    private void advancePhase(){
        if ( roundData.getRoundPartCount() == roundData.getNumberOfWords()) {
            phaseMessage(new Message(Message.Type.GOTO_REVEAL_PHASE));
            gameSession.setPhase(new RevealPhase(gameSession));
        } else {
            phaseMessage(new Message(Message.Type.GOTO_DRAW_PHASE));
            gameSession.setPhase(new DrawPhase(gameSession));
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
                gameSession.getCurrentRoundData().saveGuess(msg.player.getId(), (String) msg.data.get("guess"));
                this.submits++;
                if ( submits == roundData.getNumberOfWords() ) {
                    advancePhase();
                }
            }
            case IMAGE_DATA_RECEIVED -> {
                System.out.println("Image data received at clients side"); // Response ?
            }
        }

    }
}
