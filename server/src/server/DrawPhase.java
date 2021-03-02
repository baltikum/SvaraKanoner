package server;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private GameSettings gameSettings;
    private final HashMap<Integer, ArrayList<List<PaintPoint>>> submitedDrawings = new HashMap<>();
    private final HashMap<Integer, String> words;

    public DrawPhase(GameSession session){
        System.out.println("Wohoo! In draw phase!");
        this.gameSession = session;
        this.roundData = this.gameSession.getCurrentRoundData();
        this.gameSettings = gameSession.getGameSettings();

        timeLeft = new Timer((int) gameSettings.getDrawTimeMilliseconds(), e -> gameSession.sendMessageToAll(new Message(Message.Type.TIMES_UP)));

        words = roundData.getWordsToDraw();
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

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case SUBMIT_PICTURE-> {	    //  Hur göra här med bild, ska alltid gå till guessPhase
                // kolla medelandet
                //gameSession.getCurrentRoundData().saveImage(msg.player.getId(), (String) msg.data.get("guess"), (PaintPoint) msg.data.get("image"));
                int playerId = msg.player.getId();
                if (words.containsKey(playerId)) {
                    roundData.saveImage(playerId, words.remove(playerId),
                            (ArrayList<List<PaintPoint>>) msg.data.get("drawing"));
                    if (words.isEmpty()) {
                        advancePhase();
                    }
                }
            }
        }

    }

    private void advancePhase() {
        timeLeft.stop();
        gameSession.setPhase(new GuessPhase(gameSession));
    }
}
