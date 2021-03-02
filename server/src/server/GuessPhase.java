package server;

import common.GameSettings;
import common.Message;
import common.PaintPoint;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The GuessPhase ServerSide
 * Controls the phase, hands out image data to clients,
 * Adds all the guesses from clients into rounddata wordtracker
 *
 * @author Mattias Davidsson 20210301
 */

public class GuessPhase extends Phase {

    private GameSession gameSession;
    private RoundData roundData;
    private GameSettings settings;
    private HashMap<Integer, ArrayList<java.util.List<PaintPoint>>> guessImages;
    private int submits;

    /**
     * Contructor takes a GameSession
     * @param session
     */
    public GuessPhase(GameSession session){
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.guessImages = this.roundData.getImagesToGuessOn();
        this.settings = this.gameSession.getGameSettings();
        this.submits = 0;
        this.timeLeft = new Timer((int) settings.getGuessTimeMilliseconds(), timeOut -> advancePhase());

        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message;
            if ( !guessImages.containsKey(client.getId())) {
                message = new Message(Message.Type.GOTO);
                message.addParameter("phase","WaitingPhase");
            } else {
                message = new Message(Message.Type.GOTO);
                message.addParameter("phase","GuessPhase");
                message.addParameter("image", guessImages.get(client.getId()));
            }
            client.sendMessage(message);
        }

        timeLeft.start();
    }

    /**
     * Used to advance to next phase, chooses between Draw and Reveal phases.
     */
    private void advancePhase(){
        gameSession.getCurrentRoundData().rotateOrder();
        if ( roundData.getRoundPartCount() == roundData.getNumberOfWords()) {
            gameSession.setPhase(new RevealPhase(gameSession));
        } else {
            gameSession.setPhase(new DrawPhase(gameSession));
        }
    }

    /**
     * Increment submit by one.
     */
    private void incrementSubmit(){ this.submits++; };

    /**
     * Message handling of this phase, server side.
     * @param msg Message
     */
    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SUBMIT_GUESS -> {
                gameSession.getCurrentRoundData().saveGuess(msg.player.getId(), (String) msg.data.get("guess"));
                incrementSubmit();
                if ( submits == roundData.getNumberOfWords() ) {
                    advancePhase();
                }
            }
        }
    }
}
