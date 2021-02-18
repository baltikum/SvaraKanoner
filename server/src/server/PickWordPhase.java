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
    GameSettings settings;

    public PickWordPhase(ArrayList<ClientHandler> clients, RoundData round, GameSettings settings) {
        this.clients = clients;
        this.numberOfPlayers = clients.size();
        this.words = AllWords.getWords(numberOfPlayers);
        this.pickedWordIndex = new int[numberOfPlayers];
        this.numberOfPicks = 0;
        this.settings = settings;



    }

    /**
     * Use to increment variable
     */
    private void incrementNumberOfPicks(){
        this.numberOfPicks++;
    }

    /**
     * Generates words for the players to pick.
     * @return ArrayList<String>
     */
    public ArrayList<String> generateGuessingWords() {
        return AllWords.getWords(clients.size()*settings.getNumberOfWords());
    }






    @Override
    public void message(Message msg) {

    }


}
