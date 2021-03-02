package server;

import common.*;
import java.util.*;

public class GameSession {

    public String sessionID;
    private ArrayList<ClientHandler> connectedClients;
    private ArrayList<RoundData> sessionRounds;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private Timer timeLeft;

    public GameSession(ClientHandler host, GameSettings settings ) {
        this.sessionID = generateSessionID();
        this.gameSettings = settings;
        this.connectedClients = new ArrayList<>();
        this.sessionRounds = new ArrayList<>();

        JoinPhase joinPhase = new JoinPhase(this);
        setPhase(joinPhase);
        joinPhase.addClient(host);
    }

    public synchronized void receiveMessage(Message msg) {
        if (currentPhase != null) {
            currentPhase.message(msg);
        }
    }

    public void sendMessageToAll(Message msg) {
        for (ClientHandler handle : connectedClients)
            handle.sendMessage(msg);
    }

    public List<ClientHandler> getConnectedPlayers() {
        return connectedClients;
    }

    public ClientHandler getConnectedPlayer(int playerId) {
        for (ClientHandler c : connectedClients) {
            if (c.getId() == playerId) {
                return c;
            }
        }
        return null;
    }

    public List<RoundData> getSessionRounds() {
        return sessionRounds;
    }

    /**
     * Generates a sessionID
     * @return String
     */
    private String generateSessionID(){
        StringBuilder sessionIdBuilder = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 3; i++) { sessionIdBuilder.append((char) ('A' + random.nextInt(26))); }
        for (int i = 0; i < 3; i++) { sessionIdBuilder.append((char) ('0' + random.nextInt(10))); }
        return sessionIdBuilder.toString();
    }

    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

    /**
     * Used to retrieve current rounddata inside a phase.
     * @return RoundData.
     */
    public RoundData getCurrentRoundData(){ return sessionRounds.get((sessionRounds.size()-1));}

    public void createRoundData(HashMap<Integer,String> pickedWords) {
        sessionRounds.add(new RoundData(this, pickedWords));
    }

    /**
     * Used to retrieve the gamesettings inside a phase.
     * @return This sessions GameSettings
     */
    public GameSettings getGameSettings() { return gameSettings; }

    public void terminate() {
        for (ClientHandler c : connectedClients) {
            c.setGameSession(null);
        }
    }

}




