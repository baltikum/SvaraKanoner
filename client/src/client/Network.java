package client;

import common.GameSettings;
import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Network {

    Socket socket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    public Network() throws IOException {
        try {
            // test...
            socket = new Socket("localhost", 12345);
            System.out.println("Connected to server");


            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


            // test messages ---
            System.out.println("Send MSG");
            Message msg = new Message(Message.Type.CREATE_GAME);
            msg.addParameter("gameSettings", new GameSettings());
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();

            System.out.println("Send MSG");
            objectOutputStream.writeObject(new Message(Message.Type.JOIN_GAME));
            objectOutputStream.flush();
            // --------------

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (true) { // listen to messages from server
                try {
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println("Server message Type: " + message.type);
                    System.out.println("Message Data: " + message.data);


                    switch (message.type) {


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