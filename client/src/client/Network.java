package client;

import common.GameSettings;
import common.Message;
import common.MessageResponseListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Network extends Thread {

    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    private final Queue<MessageResponseListener> responseListeners = new ArrayDeque<>();
    private final String ipAddress;
    private final short portNumber;

    public Network(Settings settings) {
        ipAddress = settings.getIpAddress();
        //ipAddress = "localhost";
        portNumber = settings.getSocket();
    }

    public synchronized void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            objectOutputStream.reset();
        } catch (Exception e) {
            Game.game.setErrorMsg("Could not send to the server: " + e.getMessage());
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
            Game.game.setErrorMsg("Could not send to the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            System.out.println(ipAddress);
            System.out.println(portNumber);
            socket = new Socket(ipAddress, portNumber);


            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) { // listen to messages from server
                try {
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println("Received message: " + message.toString());

                    if (message.type == Message.Type.RESPONSE) {
                        if (message.error == null)
                            responseListeners.poll().onSuccess(message);
                        else
                            responseListeners.poll().onError(message.error);
                    } else {
                        Game.game.receiveMessage(message);
                    }
                } catch(SocketException ignored) {
                    break;
                } catch (Exception e) {
                    Game.game.setErrorMsg("Received invalid message: " + e.toString());
                    e.printStackTrace();
                }
            }

        } catch(Exception e) {
            Game.game.setErrorMsg("Can't connect to the server: " + e.toString());
            e.printStackTrace();
        } finally {
            Game.game.setErrorMsg("Lost connection to the server");
            try {
                socket.close();
                objectOutputStream.close();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
