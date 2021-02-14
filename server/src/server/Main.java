package server;


import common.Message;
import common.GameSettings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.net.*;


public class Main {

    public static final int PORT = 12345;

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ArrayList<GameSession> gameSessions = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        System.out.println("Server is running");
        var pool = Executors.newFixedThreadPool(100);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                ClientHandler client = new ClientHandler(listener.accept());
                pool.execute(client); // wait for new connection -> create new thread for it

                clients.add(client);
                System.out.println("New Client Joined. Clients: " + clients);
            }
        }
    }

    public static void createGameSession(ClientHandler host, GameSettings settings) {
        gameSessions.add(new GameSession(host, settings));
        Message message = new Message(Message.Type.CREATE_GAME_OK);
        host.sendMessage(message);

    }


}

