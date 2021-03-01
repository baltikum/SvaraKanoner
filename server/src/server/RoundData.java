package server;

import common.Pair;

import java.awt.Image;
import java.util.ArrayList;
import java.util.*;


/**
 * A Class that keeps track of a gamerounds data.
 *
 * Map of wordtrackers.
 *
 * Use getWordTracker to save and get data etc for words.
 *
 * @author Mattias Davidsson 20210223
 *
 *
 */

public class RoundData {

    private int numberOfWords;
    private GameSession gameSession;
    private ArrayList<Integer> playerOrder;
    private ArrayList<Integer> lastOrder;
    private ArrayList<String> wordResolver;
    private HashMap<String,WordTracker> wordMap;
    private int roundPartCount;

    /**
     * Constructor
     * @param session the gamesession
     * @param pickedWords pickedword mapped on playerIds
     */
    public RoundData(GameSession session, HashMap<Integer,String> pickedWords ){
        this.gameSession = session;
        for ( ClientHandler client : session.getConnectedPlayers()) {
            playerOrder.add(client.getId());
        }
        this.numberOfWords = pickedWords.size();
        this.wordMap = new HashMap<>();
        this.playerOrder = new ArrayList<>();
        this.lastOrder = new ArrayList<>();
        this.wordResolver = new ArrayList<>();
        this.roundPartCount = 0;

        for ( int i = 0; i < numberOfWords; i++ ) {
            int id = playerOrder.get(i);
            String str = pickedWords.get(id);
            this.wordMap.put(str, new WordTracker(id,str));
            this.wordResolver.add(str);
        }
    }

    /**
     * Used to retrieve words to draw.
     * @return HashMap, playerId maps the Word.
     */
    public HashMap<Integer,String> getWordsToDraw(){
        HashMap<Integer,String> toReturn = new HashMap<>();
        for ( int i = 0; i < numberOfWords; i++ ) {
            WordTracker temp = wordMap.get(wordResolver.get(i));
            if ( roundPartCount > 0) {
                int index = (temp.getAllGuesses().size() - 1);
                Pair tempPair = temp.getGuess(index);
                String tempGuess = tempPair.getGuess();
                toReturn.put(playerOrder.get(i), tempGuess);
            } else {
                toReturn.put(playerOrder.get(i),wordResolver.get(i));
            }
        }
        roundPartCount++;
        rotateOrder();
        return toReturn;
    }

    /**
     * Used to retrieve images for guessing in guessPhase.
     * @return HashMap playerId maps Images.
     */
    public HashMap<Integer,Image> getImagesToGuessOn(){
        HashMap<Integer,Image> toReturn = new HashMap<>();
        for ( int i = 0; i < numberOfWords; i++ ) {
            WordTracker temp = wordMap.get(wordResolver.get(i));
            int index = (temp.getAllImages().size()-1);
            Pair tempPair = temp.getDrawing(index);
            toReturn.put(playerOrder.get(i),tempPair.getImage());
        }
        return toReturn;
    }

    /**
     * Used to retrieve a list of all the words used in this round.
     * @return
     */
    public ArrayList<String> getRoundWords() { return wordResolver; }

    /**
     * Used to retrieve a words WordTracker for use of further functions inside.
     * @return wordTracker
     */
    public WordTracker getWordTracker(String word){ return wordMap.get(word); }

    /**
     * Used to save a drawn image to the wordtracker.-
     * @param id personalId of the artist.
     * @param word The word drawn
     * @param image The image
     * @return
     */
    public boolean saveImage(int id, String word, Image image ) {
        return wordMap.get(word).saveDrawing(id,image);
    }
    /**
     * Used to save a guess on a image.
     * @param id guesser ID
     * @param guess guess
     * @return boolean
     */
    public boolean saveGuess(int id, String guess ) {
        int index = playerOrder.indexOf(id);
        boolean toReturn = wordMap.get(wordResolver.get(index)).saveGuess(id,guess);

        if ( checkAnswer(guess,wordResolver.get(index)) ) {
            //gameSession.givePoint(id); funktionen existerar ej än
        }
        rotateOrder();
        roundPartCount++;
        return toReturn;
    }


    /**
     * Helper function, used to check for correct answer.
     * @param guess the guess
     * @param answer correct answer
     * @return boolean
     */
    private boolean checkAnswer(String guess ,String answer) {
        String str = guess.trim();
        str = str.toLowerCase(Locale.ROOT);
        return str.equals(answer);
    }
    /**
     * Used to rotate the order of players such that the right players get the correct data on requests.
     * Also stores lastOrder for easier finding of personal ids of who draw or guessed.
     */
    private void rotateOrder() {
        int temp = playerOrder.get(0);
        Iterator<Integer> playerIter = playerOrder.iterator();
        while(playerIter.hasNext()) {
            lastOrder.add(playerIter.next());
        }
        playerOrder.remove(0);
        playerOrder.add(temp);
    }


    public int getRoundPartCount(){ return roundPartCount; }
    /**
     * Used to check if rounds are the same as number of words. ( round over )
     *
     * @return boolean
     */
    public boolean checkRoundCount() { return roundPartCount == numberOfWords; }
    /**
     * Used to retrieve the number of words used in this round.
     * @return integer, numberOfWords
     */
    public int getNumberOfWords(){ return numberOfWords; }
    /**
     * Displays a RoundData as String.
     * @return String
     */
    public String toString() {
        StringBuilder words = new StringBuilder();
            for ( int i = 0; i < wordResolver.size(); i++ ) {
                words.append(i + wordResolver.get(i)+"\n");
            }
        return words.toString();
    }

}
