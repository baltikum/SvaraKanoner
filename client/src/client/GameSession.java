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

    /**
     * Constructs a game session.
     * @param thisPlayer The player for this client.
     * @param gameSettings The settings for the game session.
     * @param sessionId The session id string.
     */
    public GameSession(Player thisPlayer, GameSettings gameSettings, String sessionId) {
        players.add(thisPlayer);
        this.gameSettings = gameSettings;
        this.sessionId = sessionId;
    }

    /**
     * @return The common phase ui between the phases.
     */
    public PhaseUI getPhaseUI() {
        return phaseUI;
    }

    /**
     * @return The id string of the session.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Changes the current phase.
     * @param phase The phase to change to.
     */
    public void setCurrentPhase(Phase phase) {
        currentPhase = phase;
    }

    /**
     * @return The game settings for this session.
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * @return The player that this client is.
     */
    public Player getThisPlayer() {
        return players.get(0);
    }

    /**
     * @param id The id of the player to retrieve.
     * @return The player with the given id else null.
     */
    public Player getPlayerById(int id) {
        for (Player player : players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    /**
     * @return All the player in the session.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * All messages should go through here.
     * @param msg The message from the server.
     */
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
