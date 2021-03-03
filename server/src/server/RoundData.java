package server;

import common.Pair;


import java.util.ArrayList;
import java.util.*;
import common.PaintPoint;


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
    private final ArrayList<Integer> playerOrder = new ArrayList<>();
    private final ArrayList<String> wordResolver = new ArrayList<>();
    private final HashMap<String,WordTracker> wordMap = new HashMap<>();
    private int roundPartCount = 0;

    /**
     * Constructor
     * @param session the gamesession,
     * @param pickedWords pickedword mapped on playerIds
     */
    public RoundData(GameSession session, HashMap<Integer,String> pickedWords ){
        this.gameSession = session;
        this.numberOfWords = pickedWords.size();

        for ( ClientHandler client : session.getConnectedPlayers()) {
            playerOrder.add(client.getId());
        }
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
    public HashMap<Integer, WordTracker> getWordsToDraw(){
        HashMap<Integer, WordTracker> toReturn = new HashMap<>();
        for ( int i = 0; i < numberOfWords; i++ ) {
            WordTracker temp = wordMap.get(wordResolver.get(i));
            toReturn.put(playerOrder.get(i), temp);
        }
        roundPartCount++;
        rotateOrder();
        return toReturn;
    }

    /**
     * Used to retrieve images for guessing in guessPhase.
     * @return HashMap playerId maps Images.
     */
    public HashMap<Integer,ArrayList<List<PaintPoint>>> getImagesToGuessOn(){
        HashMap<Integer,ArrayList<List<PaintPoint>>> toReturn = new HashMap<>();
        for ( int i = 0; i < numberOfWords; i++ ) {
            WordTracker temp = wordMap.get(wordResolver.get(i));
            int index = (temp.getAllImages().size()-1);
            Pair tempPair = temp.getDrawing(index);
            toReturn.put(playerOrder.get(i),tempPair.getImage());
        }
        roundPartCount++;
        return toReturn;
    }

    /**
     * Used to retrieve a list of all the words used in this round.
     * @return ArrayList of strings
     */
    public ArrayList<String> getRoundWords() { return wordResolver; }

    /**
     * Used to retrieve a words WordTracker for use of further functions inside.
     * @return wordTracker
     */
    public WordTracker getWordTracker(String word){ return wordMap.get(word); }

    /**
     * Used to save a drawn image to the wordTracker.
     * @param id personalId of the artist.
     * @param word The word drawn
     * @param image The image
     * @return boolean
     */
    public boolean saveImage(int id, String word, ArrayList<List<PaintPoint>> image ) {
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
        if (toReturn && checkAnswer(guess,wordResolver.get(index))) {
            gameSession.getConnectedPlayer(id).givePoints(1);
        }
        return toReturn;
    }


    /**
     * Helper function, used to check for correct answer.
     * @param guess the guess
     * @param answer correct answer
     * @return boolean
     */
    private boolean checkAnswer(String guess, String answer) {
        guess = guess.trim().toLowerCase(Locale.ROOT);
        answer = answer.toLowerCase(Locale.ROOT);
        return guess.equals(answer);
    }

    /**
     * Used to rotate the order of players such that the right players get the correct data on requests.
     * Also stores lastOrder for easier finding of personal ids of who draw or guessed.
     */
    public void rotateOrder() {
        playerOrder.add(playerOrder.remove(0));
    }

    /**
     * Used to retrieve this roundDatas round count.
     * @return integer
     */
    public int getRoundPartCount(){ return roundPartCount; }

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
            words.append(i).append(wordResolver.get(i)).append("\n");
        }
        return words.toString();
    }

}
