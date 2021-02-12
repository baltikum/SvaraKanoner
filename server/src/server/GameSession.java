package server;

import common.GameSettings;
import common.Phase;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameSession {

    private Socket host;
    private ArrayList<Socket> clients;
    private ArrayList<RoundData> sessionRounds;
    private ArrayList<Integer> points;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private int nbrPlayers;

    public GameSession(String host, int port, GameSettings settings ) throws IOException {
        this.host = new Socket(host, port);
        this.gameSettings = settings;
        this.currentPhase = new JoinPhase();
        this.clients = new ArrayList<Socket>();
        this.sessionRounds = new ArrayList<RoundData>();
        this.points = new ArrayList<Integer>();
        this.nbrPlayers = 1;
    }

    public void addClient(Socket client ) {}
    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

}




