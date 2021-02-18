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


    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (Exception ignore) {

        }
    }

    public void sendMessage(Message message, MessageResponseListener responseListener) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            responseListeners.add(responseListener);
        } catch (Exception ignore) {

        }
    }

    public void run() {
        try {
            socket = new Socket("localhost", 12345);
            System.out.println("Connected to server");

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
                    break;
                }
            }

        } catch(Exception e) {
            System.out.println("Can't connect to server " + e);
        } finally {
            System.out.println("Lost connection to server");
            try {
                socket.close();
                objectOutputStream.close();
                objectInputStream.close();
            } catch (Exception e) {

            }
        }
    }

}
