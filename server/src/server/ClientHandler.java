package server;

import common.*;
import java.io.*;
import java.net.*;

public class ClientHandler extends Player implements Runnable {

    private final Socket socket;
    private ObjectOutputStream objectOutputStream;
    private GameSession gameSession;
    private int points;

    public ClientHandler(Socket socket, int id) {
        super(id, "", -1);
        this.socket = socket;
    }

    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.reset();
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
                    if (message.type == Message.Type.CREATE_GAME)
                        Main.createGameSession(message);
                    else if (message.type == Message.Type.JOIN_GAME)
                        Main.joinGame(message);
                    else if (gameSession != null) {
                        if (message.type == Message.Type.CHAT_MESSAGE) {
                            Message msg = new Message(Message.Type.CHAT_MESSAGE);
                            msg.addParameter("message", message.data.getOrDefault("message", "Hello, sailor!"));
                            for (ClientHandler c : gameSession.getConnectedPlayers()) {
                                if (c.getId() != this.getId()) {
                                    c.sendMessage(msg);
                                }
                            }
                        } else {
                            gameSession.receiveMessage(message);
                        }
                    }
                } catch (EOFException | SocketException ignore) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
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
        points = 0;
        gameSession = session;
    }

    public void givePoints(int amount) {
        points += amount;
    }

    public int getPoints() {
        return points;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}