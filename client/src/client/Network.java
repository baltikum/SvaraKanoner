package client;

import common.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Network extends Thread {

    public interface ConnectedListener {
        void connectionSuccess();
        void connectionFailed();
    }

    private final Game game;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final Queue<MessageResponseListener> responseListeners = new ArrayDeque<>();
    private ConnectedListener connectedListener;
    private boolean isConnected = false;

    public Network(Game game, ConnectedListener listener) {
        this.game = game;
        connectedListener = listener;
    }

    public synchronized void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (Exception e) {
            game.setErrorMsg("Could not send to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(Message message, MessageResponseListener responseListener) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.reset();
            responseListeners.add(responseListener);
        } catch (Exception e) {
            game.setErrorMsg("Could not send to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Settings settings = Settings.getSettings();
            socket = new Socket(settings.getIpAddress(), settings.getSocket());

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) { // listen to messages from server
                try {
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println("Received message: " + message.toString());

                    if (message.type == Message.Type.CONNECTION_SUCCESS) {
                        connectedListener.connectionSuccess();
                        connectedListener = null;
                        isConnected = true;
                    } else if (message.type == Message.Type.RESPONSE) {
                        if (message.error == null)
                            responseListeners.poll().onSuccess(message);
                        else
                            responseListeners.poll().onError(message.error);
                    } else {
                        GameSession session = game.getSession();
                        if (session != null) {
                            session.receiveMessage(message);
                        }
                    }
                } catch(SocketException ignored) {
                    break;
                } catch (Exception e) {
                    game.setErrorMsg("Received invalid message: " + e.toString());
                    e.printStackTrace();
                }
            }
        } catch (ConnectException ignore) {
        } catch (Exception e) {
            game.setErrorMsg("Can't connect to the server: " + e.toString());
            e.printStackTrace();
        } finally {
            game.setErrorMsg("Lost connection to the server");
            try {
                if (socket != null) socket.close();
                if (objectOutputStream != null) objectOutputStream.close();
                if (objectInputStream != null) objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isConnected = false;
            if (connectedListener != null) {
                connectedListener.connectionFailed();
            }
        }
    }

    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
