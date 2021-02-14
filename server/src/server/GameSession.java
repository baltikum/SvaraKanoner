package server;

import common.GameSettings;
import common.Phase;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameSession {

    private ClientHandler host;
    private ArrayList<ClientHandler> clients;
    private ArrayList<RoundData> sessionRounds;
    private ArrayList<Integer> points;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private int nbrPlayers;

    public GameSession(ClientHandler host, GameSettings settings ) {
        this.host = host;
        this.gameSettings = settings;
        this.currentPhase = new JoinPhase();
        this.clients = new ArrayList<ClientHandler>();
        this.sessionRounds = new ArrayList<RoundData>();
        this.points = new ArrayList<Integer>();
        this.nbrPlayers = 1;
    }

    public void addClient(ClientHandler client ) {
        clients.add(client);
    }
    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

}




