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

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
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


    private static class ClientHandler implements Runnable {

        private String name;
        private Socket socket;
        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                // test msg
                System.out.println("Send MSG");
                objectOutputStream.writeObject(new Message(Message.Type.JOIN_LOBBY));
                objectOutputStream.flush();


                while (true) { // listen to messages loop
                    try {
                        Message message = (Message) objectInputStream.readObject();
                        synchronized (System.out) {
                            System.out.println("Client message Type: " + message.type);
                            System.out.println("Message Data: " + message.data);

                        }
                        switch (message.type) {
                            case CREATE_LOBBY -> System.out.println("Lobby name: " + message.data.get("lobbyName"));

                        }

                    } catch (Exception e) {
                        break;
                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally { // Client left / disconnected
                clients.remove(this); // volentile / synchronized ???
                synchronized (System.out) {
                    System.out.println("Client left Clients: " + clients);
                }
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }


        }
    }
}

