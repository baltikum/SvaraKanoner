package client;

import common.GameSettings;
import common.Message;
import common.MessageResponseListener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
        portNumber = settings.getSocket();
    }

    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (Exception ignore) {
            Game.game.setErrorMsg("Could not send to the server");
        }
    }

    public void sendMessage(Message message, MessageResponseListener responseListener) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            responseListeners.add(responseListener);
        } catch (Exception ignore) {
            Game.game.setErrorMsg("Could not send to the server");
        }
    }

    public void run() {
        try {
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
                } catch (Exception e) {
                    Game.game.setErrorMsg("Received invalid message");
                }
            }

        } catch(Exception e) {
            Game.game.setErrorMsg("Can't connect to the server: " + e.getMessage());
        } finally {
            Game.game.setErrorMsg("Lost connection to the server");
            try {
                socket.close();
                objectOutputStream.close();
                objectInputStream.close();
            } catch (Exception e) {

            }
        }
    }

}
