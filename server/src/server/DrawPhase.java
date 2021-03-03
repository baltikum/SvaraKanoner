package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Timer;

import common.Message;
import common.PaintPoint;
import common.Phase;

public class DrawPhase extends Phase {

    private final GameSession gameSession;
    private final RoundData roundData;
    private final HashMap<Integer, WordTracker> words;

    public DrawPhase(GameSession session){
        System.out.println("Wohoo! In draw phase!");
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();

        timeLeft = new Timer((int) gameSession.getGameSettings().getDrawTimeMilliseconds(), e -> {
            gameSession.sendMessageToAll(new Message(Message.Type.TIMES_UP));
            timeLeft.stop();
        });
        timeLeft.start();

        words = roundData.getWordsToDraw();
        for (ClientHandler client: gameSession.getConnectedPlayers()) {
            Message message = new Message(Message.Type.GOTO);
            if ( !words.containsKey(client.getId())) {
                message.addParameter("phase","WaitingPhase");
            } else {
                message.addParameter("phase","DrawPhase");
                message.addParameter("word", words.get(client.getId()).getLatestGuess());
            }
            client.sendMessage(message);
        }
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.SUBMIT_PICTURE) {
            int playerId = msg.player.getId();
            if (words.containsKey(playerId)) {
                WordTracker tracker = words.remove(playerId);
                roundData.saveImage(playerId, tracker.getWord(),
                        (ArrayList<List<PaintPoint>>) msg.data.get("drawing"));
                if (words.isEmpty()) {
                    advancePhase();
                }
            }
        }
    }

    private void advancePhase() {
        timeLeft.stop();
        gameSession.setPhase(new GuessPhase(gameSession));
    }
}
