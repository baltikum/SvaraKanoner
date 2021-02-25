package server;

import common.*;
import java.util.*;

public class GameSession {

    public String sessionID;
    private ClientHandler host;
    private ArrayList<ClientHandler> connectedClients;
    private ClientHandler[] activeClients;
    private ArrayList<RoundData> sessionRounds;
    private ArrayList<Integer> points;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private Timer timeLeft;

    public GameSession(ClientHandler host, GameSettings settings ) {
        this.sessionID = generateSessionID();
        this.host = host;
        this.gameSettings = settings;
        this.connectedClients = new ArrayList<>();
        this.sessionRounds = new ArrayList<>();
        this.points = new ArrayList<>();

        JoinPhase joinPhase = new JoinPhase(this);
        setPhase(joinPhase);
        joinPhase.addClient(host);
    }

    public void receiveMessage(Message msg) {
        switch (msg.type) {
            default -> {
                if (currentPhase != null) {
                    currentPhase.message(msg);
                }
            }
        }
    }

    public void sendMessageToAll(Message msg) {
        for (ClientHandler handle : connectedClients)
            handle.sendMessage(msg);
    }

    public List<ClientHandler> getConnectedPlayers() {
        return connectedClients;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
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

}




