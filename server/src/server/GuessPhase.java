package server;

import common.GameSettings;
import common.Message;
import common.PaintPoint;
import common.Phase;

import javax.swing.*;
import java.awt.*;
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
    private HashMap<Integer, PaintPoint> guessImages;
    private int submits;
    private HashMap<Integer,Boolean> dataRecievedCount;
    private Timer resendCheck;

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
        this.dataRecievedCount = new HashMap<>();
        this.timeLeft = new Timer((int) settings.getGuessTimeMilliseconds(), timeOut -> advancePhase());
        this.resendCheck = new Timer(3000, timeOut -> checkForResend());

        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message;
            if ( guessImages.containsKey(client.getId())) {
                message = new Message(Message.Type.GOTO_GUESS_PHASE);
            } else {
                message = new Message(Message.Type.GOTO_WAIT_PHASE);
            }
            client.sendMessage(message);
        }

        sendImages();

        timeLeft.start();
        resendCheck.start();
    }

    /**
     * Timer calls this if time runs out for a client to acknowledge it recieved its image.
     * If not it restarts the timer and initiates a resend of the images.
     */
    private void checkForResend(){
        if ( dataRecievedCount.size() == guessImages.size() ) {
            System.out.println("All images were received at clientside");
        } else {
            sendImages();
            resendCheck.restart();
        }
    }

    /**
     * Function sends the images out to clients. Can be used initially or to resend data.
     */
    private void sendImages(){
        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.IMAGE_DATA);
            int id = client.getId();
            if ( !dataRecievedCount.containsKey(id) ) { // Kollar ifall bilden redan är överförd.
                if ( guessImages.containsKey(id)) {
                    //    message.addParameter("image", guessImages.get(client.getId())); // Egen klass med linjer för att generera bild.
                    client.sendMessage(message);
                }
            }
        }
    }

    /**
     * Used to advance to next phase, chooses between Draw and Reveal phases.
     */
    private void advancePhase(){
        if ( roundData.getRoundPartCount() == roundData.getNumberOfWords()) {
            gameSession.sendMessageToAll(new Message(Message.Type.GOTO_REVEAL_PHASE));
            gameSession.setPhase(new RevealPhase(gameSession));
        } else {
            gameSession.sendMessageToAll(new Message(Message.Type.GOTO_DRAW_PHASE));
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
                this.dataRecievedCount.put(msg.player.getId(),true);
                System.out.println("Image data received from client id : " + msg.player.getId());

                if ( dataRecievedCount.size() == guessImages.size() ) {
                    resendCheck.stop(); // terminate the timer if all images were received.
                }
            }
        }
    }
}
