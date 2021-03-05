package client;

import common.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Network handles the communication between the client and server client side.
 *
 * @author Lukas Magnusson
 * @version 05/03/21
 */
public class Network extends Thread {

    /**
     * ConnectedListener can be implemented to get a response about the success/failure to connect to a server.
     */
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

    /**
     * Creates a new instance, does not try to connect until the thread is started.
     *
     * @param game The game instance.
     * @param listener The listener to get information about the success of the connection.
     */
    public Network(Game game, ConnectedListener listener) {
        this.game = game;
        connectedListener = listener;
    }

    /**
     * Send a message to the server.
     * @param message The message.
     */
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

    /**
     * Sends a message that expects message of type RESPONSE back.
     * @param message The message.
     * @param responseListener Called when the RESPONSE message gets back.
     */
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

    /**
     * Waits for messages from the server until the socket is closed or times out.
     */
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

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return True if the connection has connected once and not been closed or timed out, else false.
     */
    public boolean isConnected() {
        return isConnected;
    }
}
