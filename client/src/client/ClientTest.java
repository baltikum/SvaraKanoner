package client;

import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTest {

    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    public ClientTest() throws IOException {
        try {
            // test...
            socket = new Socket("localhost", 12345);
            System.out.println("Connected to server");


            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


            // test messages ---
            System.out.println("Send MSG");
            Message msg = new Message(Message.Type.CREATE_LOBBY);
            msg.addParameter("lobbyName", "The best lobby");
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();

            System.out.println("Send MSG");
            objectOutputStream.writeObject(new Message(Message.Type.JOIN_LOBBY));
            objectOutputStream.flush();
            // --------------

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (true) { // listen to messages from server
                try {
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println("Client message Type: " + message.type);
                    System.out.println("Message Data: " + message.data);


                    switch (message.type) {
                        case CREATE_LOBBY -> System.out.println("Lobby name: " + message.data.get("lobbyName"));

                    }

                } catch (Exception e) {
                    break;
                }


            }

        } finally {
            System.out.println("Lost connection to server");
            socket.close();
            objectOutputStream.close();
            objectInputStream.close();
        }




    }

}
