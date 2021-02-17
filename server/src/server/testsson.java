package server;

import common.GameSettings;
import common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;

public class testsson {

    public static final int PORT = 12345;

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ArrayList<GameSession> gameSessions = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        for ( int i = 0; i < 50; i++ ) {
            printRandom();
        }

        }

        private static void printRandom() {
            Random random = new Random();
            ArrayList<Character> characters= new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                characters.add((char) (65 + random.nextInt(26)));
            }
            for (int i = 0; i < 3; i++) {
                characters.add((char) (48 + random.nextInt(10)));
            }


            String sessionID = "";
            Iterator<Character> print = characters.iterator();
            while (print.hasNext()) {
                sessionID +=print.next();
            }
            System.out.print(sessionID + "\n");
        }

}




