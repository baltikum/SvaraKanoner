package server;

import common.GameSettings;
import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

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
            objectOutputStream.writeObject(new Message(Message.Type.JOIN_GAME));
            objectOutputStream.flush();


            while (true) { // listen to messages loop
                try {
                    Message message = (Message) objectInputStream.readObject();
                    synchronized (System.out) {
                        System.out.println("Received message Type: " + message.type);

                    }
                    switch (message.type) {
                        case CREATE_GAME:
                            GameSettings gameSettings = (GameSettings)message.data.get("gameSettings");
                            System.out.println("Game settings: " + gameSettings);
                            // create new gameSession -> send OK to client
                            Main.createGameSession(this, gameSettings);
                            // send message OK


                            break;





                    }

                } catch (Exception e) {
                    break;
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally { // Client left / disconnected
            Main.clients.remove(this); // volentile / synchronized ???
            synchronized (System.out) {
                System.out.println("Client left. Clients: " + Main.clients);
            }
            try {
                socket.close();
            } catch (IOException e) {

            }
        }


    }
}