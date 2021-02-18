package server;


import common.Message;
import common.GameSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class Main {

    public static final int PORT = 12345;

    private static ArrayList<GameSession> gameSessions = new ArrayList<>();

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

    public static void createGameSession(Message msg) {
        ClientHandler host = (ClientHandler) msg.player;
        GameSession gameSession = new GameSession(host, (GameSettings) msg.data.get("settings"));
        gameSessions.add(gameSession);

        // Respond
        Message message = new Message(Message.Type.CREATE_GAME_RESPONSE);
        message.data.put("id", host.getId());
        message.data.put("name", host.getName());
        message.data.put("code", gameSession.getCode());
        host.sendMessage(message);
    }

    public static void joinGame(Message msg) {
        String gameCode = (String) msg.data.get("code");
        ClientHandler joiner = (ClientHandler) msg.player;
        String errorMsg = null;
        if (joiner.getGameSession() != null) {
            errorMsg = "You are already in a game session. "; // Can this happen?
        } else {
            GameSession joinedSession = null;
            for (GameSession session : gameSessions) {
                if (session.getCode().equals(gameCode)) {
                    joinedSession = session;
                    break;
                }
            }
            if (joinedSession == null) {
                errorMsg = "Invalid invite code. ";
            } else {
                joinedSession.receiveMessage(msg);
            }
        }
        if (errorMsg != null) {
            Message errorResponse = new Message(Message.Type.JOIN_GAME_RESPONSE);
            errorResponse.error = errorMsg;
            joiner.sendMessage(errorResponse);
        }
    }
}

