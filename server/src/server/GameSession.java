package server;

import common.GameSettings;
import common.Phase;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class GameSession {

    public String sessionID;
    private ClientHandler host;
    private ArrayList<ClientHandler> clients;
    private ArrayList<RoundData> sessionRounds;
    private ArrayList<Integer> points;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private Timer timeLeft;
    private int nbrPlayers;

    public GameSession(ClientHandler host, GameSettings settings ) {
        this.sessionID = generateSessionID();
        this.host = host;
        this.gameSettings = settings;
        this.currentPhase = new JoinPhase();
        this.clients = new ArrayList<ClientHandler>();
        this.sessionRounds = new ArrayList<RoundData>();
        this.points = new ArrayList<Integer>();
        this.nbrPlayers = 1;

        addClient(host);
    }

    /**
     * Generates a sessionID
     * @return String
     */
    private String generateSessionID(){
        String sessionID = "";
        Random random = new Random();
        ArrayList<Character> characters= new ArrayList<>();

        for (int i = 0; i < 3; i++) { characters.add((char) (65 + random.nextInt(26))); }
        for (int i = 0; i < 3; i++) { characters.add((char) (48 + random.nextInt(10))); }

        Iterator<Character> print = characters.iterator();
        while (print.hasNext()) {
            sessionID +=print.next();
        }
        return sessionID;
    }
    public void addClient(ClientHandler client ) {
        clients.add(client);
    }
    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

}




