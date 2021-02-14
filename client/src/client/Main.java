package client;


import java.io.IOException;

public class Main {

    public static void main(String[] args){

        try {
            new Network();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
