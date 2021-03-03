package server;

import common.GameSettings;
import common.Message;
import common.PaintPoint;
import common.Phase;

import javax.swing.*;
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

    /**
     * Contructor takes a GameSession
     * @param session
     */
    public GuessPhase(GameSession session) {
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.guessImages = this.roundData.getImagesToGuessOn();
        this.settings = this.gameSession.getGameSettings();
        this.timeLeft = new Timer((int) settings.getGuessTimeMilliseconds(),
                timeOut -> session.sendMessageToAll(new Message(Message.Type.TIMES_UP)));

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
    private void advancePhase() {
        timeLeft.stop();
        gameSession.getCurrentRoundData().rotateOrder();

        if ( roundData.getRoundPartCount() == roundData.getNumberOfWords()) {
            gameSession.setPhase(new RevealPhase(gameSession));
        } else {
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
            case SUBMIT_GUESS -> {
                if (roundData.saveGuess(msg.player.getId(), (String) msg.data.get("guess")) ) {
                    advancePhase();
                }
            }
        }
    }
}
