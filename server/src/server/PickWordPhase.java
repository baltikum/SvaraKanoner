package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import java.util.Timer;
import java.util.ArrayList;

public class PickWordPhase extends Phase {
    private ArrayList<ClientHandler> clients;
    private int numberOfPlayers;
    private ArrayList<String> words;
    private int pickedWordIndex[];
    private int numberOfPicks;
    private Timer timeLeft;
    GameSettings settings;

    public PickWordPhase(ArrayList<ClientHandler> clients, RoundData round, GameSettings settings) {
        this.clients = clients;
        this.numberOfPlayers = clients.size();
        this.words = AllWords.getWords(numberOfPlayers);
        this.pickedWordIndex = new int[numberOfPlayers];
        this.numberOfPicks = 0;
        this.settings = settings;



    }

    private void incrementNumberOfPicks(){
        this.numberOfPicks++;
    }

    public ArrayList<String> generateGuessingWords() {
        return AllWords.getWords(clients.size()*settings.getNumberOfWords());
    }


    @Override
    public void message(Message msg) {

    }


}
