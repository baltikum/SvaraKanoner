package client;


import common.GameSettings;
import common.Message;
import common.Phase;
import common.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles one game session from start to finish.
 * Any shared data between the phases should be in here.
 *
 * @author Jesper Jansson
 * @version 03/03/21
 */
public class GameSession {

    private Phase currentPhase = null;

    private final PhaseUI phaseUI = new PhaseUI();

    private final String sessionId;
    private final GameSettings gameSettings;

    private final List<Player> players = new ArrayList<>();

    public GameSession(Player thisPlayer, GameSettings gameSettings, String sessionId) {
        players.add(thisPlayer);
        this.gameSettings = gameSettings;
        this.sessionId = sessionId;
    }

    public PhaseUI getPhaseUI() {
        return phaseUI;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setCurrentPhase(Phase phase) {
        currentPhase = phase;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public Player getThisPlayer() {
        return players.get(0);
    }

    public Player getPlayerById(int id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void receiveMessage(Message msg) {
        if (msg.type == Message.Type.CHAT_MESSAGE) {
            Game.getInstance().getChat().message(msg);
        } else if (msg.type == Message.Type.GOTO) {
            String targetPhase = (String) msg.data.get("phase");
            switch (targetPhase) {
                case "PickWordPhase" -> {
                    setCurrentPhase(new PickWordPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("PickWord");
                }
                case "DrawPhase" -> {
                    setCurrentPhase(new DrawPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("Draw");
                }
                case "GuessPhase" -> {
                    setCurrentPhase(new GuessPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("Guess");
                }
                case "RevealPhase" -> {
                    setCurrentPhase(new RevealPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("Reveal");
                }
                case "WaitingPhase" -> {
                    setCurrentPhase(new WaitingPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("Waiting");
                }
                case "WinnerPhase" -> {
                    setCurrentPhase(new WinnerPhase(msg));
                    AudioPlayer.getInstance().changeSongAudioPlayer("Winner");
                }
            }
        } else {
            if (currentPhase != null) currentPhase.message(msg);
        }
    }


}
