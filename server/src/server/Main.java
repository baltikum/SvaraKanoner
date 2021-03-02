package server;


import common.Message;
import common.GameSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class Main {

    public static final int PORT = 12345;

    private static final ArrayList<GameSession> gameSessions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server is running");
        var pool = Executors.newFixedThreadPool(100);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                ClientHandler client = new ClientHandler(listener.accept());
                pool.execute(client); // wait for new connection -> create new thread for it
            }
        }
    }

    public static synchronized void createGameSession(Message msg) {
        ClientHandler host = (ClientHandler) msg.player;
        host.setName((String) msg.data.getOrDefault("requestPlayerName", ""));
        host.setAvatarId((int) msg.data.getOrDefault("requestAvatarId", 0));
        GameSession gameSession = new GameSession(host, (GameSettings) msg.data.get("settings"));
        gameSessions.add(gameSession);

        // Respond
        Message message = new Message(Message.Type.RESPONSE);
        message.addParameter("playerId", host.getId());
        message.addParameter("playerName", host.getName());
        message.addParameter("sessionId", gameSession.sessionID);
        host.sendMessage(message);
    }

    public static synchronized void joinGame(Message msg) {
        String gameCode = (String) msg.data.getOrDefault("sessionId", "");
        ClientHandler joiner = (ClientHandler) msg.player;
        GameSession joinedSession = null;
        for (GameSession session : gameSessions) {
            if (session.sessionID.equals(gameCode)) {
                joinedSession = session;
                break;
            }
        }
        if (joinedSession == null) {
            Message errorResponse = new Message(Message.Type.RESPONSE);
            errorResponse.error = "Invalid session id. ";
            joiner.sendMessage(errorResponse);
        } else {
            joinedSession.receiveMessage(msg);
        }
    }

    public static synchronized void removeSession(GameSession session) {
        gameSessions.remove(session);
    }
}

