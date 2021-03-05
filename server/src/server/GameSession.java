package server;

import common.*;
import java.util.*;

/**
 * Keeps track of the common information and logic between the phases server side.
 * Any messages not handled by this class is relayed to the current phase.
 *
 * @author Mattias Davidsson
 * @version 05/03/21
 */
public class GameSession {

    public String sessionID;
    private ArrayList<ClientHandler> connectedClients;
    private ArrayList<RoundData> sessionRounds;
    private GameSettings gameSettings;
    private Phase currentPhase;

    /**
     * Constructor
     * @param host ClientHandler
     * @param settings GameSettings
     */
    public GameSession(ClientHandler host, GameSettings settings ) {
        this.sessionID = generateSessionID();
        this.gameSettings = settings;
        this.connectedClients = new ArrayList<>();
        this.sessionRounds = new ArrayList<>();

        JoinPhase joinPhase = new JoinPhase(this);
        setPhase(joinPhase);
        joinPhase.addClient(host);
    }

    /**
     * Passes a recieved message toward respective active phase.
     * @param msg the message
     */
    public synchronized void receiveMessage(Message msg) {
        if (currentPhase != null) {
            currentPhase.message(msg);
        }
    }

    /**
     * Send message to all connected clients.
     * @param msg the message.
     */
    public void sendMessageToAll(Message msg) {
        for (ClientHandler handle : connectedClients)
            handle.sendMessage(msg);
    }

    /**
     * Returns a list of the connected clients in this session.
     * @return ClientHandlers
     */
    public List<ClientHandler> getConnectedPlayers() {
        return connectedClients;
    }

    /**
     * Retrieves a clients ClientHandler from this session via its player id.
     * @param playerId
     * @return ClientHandler
     */
    public ClientHandler getConnectedPlayer(int playerId) {
        for (ClientHandler c : connectedClients) {
            if (c.getId() == playerId) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retrieves all the played RoundDatas for this session.
     * @return
     */
    public List<RoundData> getSessionRounds() {
        return sessionRounds;
    }

    /**
     * Generates a sessionID  ex. LXL123
     * Used by players to identify joining this session.
     * @return String
     */
    private String generateSessionID(){
        StringBuilder sessionIdBuilder = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 3; i++) { sessionIdBuilder.append((char) ('A' + random.nextInt(26))); }
        for (int i = 0; i < 3; i++) { sessionIdBuilder.append((char) ('0' + random.nextInt(10))); }
        return sessionIdBuilder.toString();
    }

    /**
     * Use to set the current serverside phase.
     * @param newPhase
     */
    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

    /**
     * Used to retrieve current rounddata inside a phase.
     * @return RoundData.
     */
    public RoundData getCurrentRoundData(){ return sessionRounds.get((sessionRounds.size()-1));}

    /**
     * Used to create a new RoundData inside this Gamesession.
     * @param pickedWords
     */
    public void createRoundData(HashMap<Integer,String> pickedWords) {
        sessionRounds.add(new RoundData(this, pickedWords));
    }

    /**
     * Used to retrieve the gamesettings inside a phase.
     * @return This sessions GameSettings
     */
    public GameSettings getGameSettings() { return gameSettings; }

    /**
     * Use to terminate this gamesession when game is over.
     */
    public void terminate() {
        for (ClientHandler c : connectedClients) {
            c.setGameSession(null);
        }
    }

}




