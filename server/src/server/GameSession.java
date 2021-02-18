package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GameSession {

    private ClientHandler host;
    private ArrayList<ClientHandler> connectedClients;
    private ClientHandler[] activeClients;
    private ArrayList<RoundData> sessionRounds;
    private ArrayList<Integer> points;
    private GameSettings gameSettings;
    private Phase currentPhase;
    private String code = "ABC123";

    public GameSession(ClientHandler host, GameSettings settings ) {
        this.host = host;
        this.gameSettings = settings;
        this.currentPhase = new JoinPhase();
        this.connectedClients = new ArrayList<>();
        this.sessionRounds = new ArrayList<>();
        this.points = new ArrayList<>();

        // TODO: Generate code.

        setPhase(new JoinPhase());
        this.connectedClients.add(host);
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

    public void addClient(ClientHandler client) {
        connectedClients.add(client);
    }

    public void setPhase(Phase newPhase ) { currentPhase = newPhase; }

    public String getCode() {
        return code;
    }

}




