package server;

import java.util.*;
import common.*;


/**
 * The DrawPhase on the sever.
 * Controls the phase, hands out words to paint for clients.
 * Advances to GuessPhase when all players have sent their drawings.
 *
 * @author Johnny Larsson.
 * @version 04/03/21
 */

public class DrawPhase implements Phase {

    private final GameSession gameSession;
    private final RoundData roundData;
    private final Timer timeLeft;

    /**
     * Constructs a GuessPhase and tells all clients to goto it.
     * Also handles if a player should go to the WaitingPhase if the number of players are uneven.
     * @param session The game session that should go to DrawPhase.
     */
    public DrawPhase(GameSession session){
        System.out.println("Wohoo! In draw phase!");
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();


        timeLeft = new Timer();
        timeLeft.schedule(new TimerTask() {
                              @Override
                              public void run() {
                                  gameSession.sendMessageToAll(new Message(Message.Type.TIMES_UP));
                              }
                          }, gameSession.getGameSettings().getDrawTimeMilliseconds());

        HashMap<Integer, String> words = roundData.getWordsToDraw();
        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.GOTO);
            if ( !words.containsKey(client.getId())) {
                message.addParameter("phase","WaitingPhase");
            } else {
                message.addParameter("phase","DrawPhase");
                message.addParameter("word", words.get(client.getId()));
            }
            client.sendMessage(message);
        }
    }

    /**
     * Message handling of this phase, server side.
     * @param msg
     */
    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.SUBMIT_PICTURE) {
            int playerId = msg.player.getId();
            if (roundData.saveImage(playerId, (ArrayList<List<PaintPoint>>) msg.data.get("drawing"))) {
                advancePhase();
            }
        }
    }

    /**
     * Advances to the next phase that is the GuessPhase
     */
    private void advancePhase() {
        timeLeft.cancel();
        gameSession.setPhase(new GuessPhase(gameSession));
    }
}
