package client;

import common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTest {

    public ClientTest() throws IOException {
        // test...
        Socket socket = new Socket("localhost", 12345);
        System.out.println("Connected to server");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        System.out.println("Send MSG");
        Message message = new Message(Message.Type.CREATE_LOBBY);
        message.addParameter("lobbyName", "The best lobby");
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();

        System.out.println("Send MSG");
        objectOutputStream.writeObject(new Message(Message.Type.JOIN_LOBBY));
        objectOutputStream.flush();


        socket.close();

    }

}
