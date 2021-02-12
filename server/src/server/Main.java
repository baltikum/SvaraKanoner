package server;

import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.net.*;

public class Main {

    public static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        System.out.println("Server is running");
        var pool = Executors.newFixedThreadPool(100);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                pool.execute(new ClientHandler(listener.accept())); // wait for new connection -> create new thread for it
            }
        }
    }


    private static class ClientHandler implements Runnable {

        private String name;
        private Socket socket;
        private ObjectInputStream objectInputStream;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());

                while (true) { // listen to messages loop
                    try {
                        Message message = (Message) objectInputStream.readObject();
                        synchronized (System.out) {
                            System.out.println("Client message Type: " + message.type);
                            System.out.println("Message Data: " + message.data);

                            switch (message.type) {
                                case CREATE_LOBBY -> System.out.println("Lobby name: " + message.data.get("lobbyName"));

                            }


                        }
                    } catch (Exception e) {

                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}

