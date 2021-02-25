package server;

import common.Message;
import common.Player;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Player implements Runnable {

    private final Socket socket;
    private ObjectOutputStream objectOutputStream;
    private GameSession gameSession;

    public ClientHandler(Socket socket) {
        super(-1, "", -1);
        this.socket = socket;
    }

    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (Exception e) {

        }

    }

    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            while (true) { // listen to messages loop
                try {
                    Message message = (Message) objectInputStream.readObject();
                    message.player = this;
                    synchronized (System.out) {
                        System.out.println("Received message: " + message.toString());
                    }

                    switch (message.type) {
                        case SUBMIT_GUESS-> {
                            gameSession.getCurrentRoundData().saveGuess(getId(), (String) message.data.get("guess"));
                        }
                        case IMAGE_DATA_RECEIVED -> {
                            System.out.println("Image data received at clients side");
                        }
                    }



                    if (message.type == Message.Type.CREATE_GAME)
                        Main.createGameSession(message);
                    else if (message.type == Message.Type.JOIN_GAME)
                        Main.joinGame(message);
                    else if (gameSession != null)
                        gameSession.receiveMessage(message);
                } catch (Exception e) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally { // Client left / disconnected
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public void setGameSession(GameSession session) {
        gameSession = session;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}