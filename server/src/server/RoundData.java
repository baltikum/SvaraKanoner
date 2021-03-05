package server;

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
 * @version 2021/02/23
 *
 */

public class RoundData {

    private final GameSession gameSession;
    private final ArrayList<Integer> playerOrder = new ArrayList<>();
    private final ArrayList<WordTracker> wordTrackers = new ArrayList<>();
    private int roundPartCount = 0;
    private int saved = 0;

    /**
     * Constructor
     * @param session the gamesession,
     * @param pickedWords pickedword mapped on playerIds
     */
    public RoundData(GameSession session, HashMap<Integer,String> pickedWords ){
        this.gameSession = session;

        List<ClientHandler> clients = new ArrayList<>(session.getConnectedPlayers());
        Collections.shuffle(clients);
        for ( ClientHandler client : clients) {
            playerOrder.add(client.getId());
        }
        for ( int i = 0; i < pickedWords.size(); i++ ) {
            int id = playerOrder.get(i);
            String str = pickedWords.get(id);
            wordTrackers.add(new WordTracker(id,str));
        }
    }

    /**
     * Used to retrieve words to draw.
     * @return HashMap, playerId maps the Word.
     */
    public HashMap<Integer, String> getWordsToDraw(){
        HashMap<Integer, String> toReturn = new HashMap<>();
        for ( int i = 0; i < wordTrackers.size(); i++ ) {
            toReturn.put(playerOrder.get(i), wordTrackers.get(i).getLatestGuess());
        }
        roundPartCount++;
        return toReturn;
    }

    /**
     * Used to retrieve images for guessing in guessPhase.
     * @return HashMap playerId maps Images.
     */
    public HashMap<Integer, ArrayList<List<PaintPoint>>> getImagesToGuessOn(){
        HashMap<Integer, ArrayList<List<PaintPoint>>> toReturn = new HashMap<>();
        for ( int i = 0; i < wordTrackers.size(); i++ ) {
            toReturn.put(playerOrder.get(i), wordTrackers.get(i).getLatestImage());
        }
        roundPartCount++;
        return toReturn;
    }

    /**
     * Gets a word tracker at the given index
     * @return The word tracker
     */
    public WordTracker getWordTracker(int index) { return wordTrackers.get(index); }

    /**
     * Used to save a drawn image to the wordTracker.
     * @param id personalId of the artist.
     * @param image The image
     * @return boolean true if all images for this round is saved.
     */
    public boolean saveImage(int id, ArrayList<List<PaintPoint>> image ) {
        int index = playerOrder.indexOf(id);
        WordTracker tracker = wordTrackers.get(index);

        boolean success = tracker.saveDrawing(id,image);
        if ( success ) {
            this.saved++;
            if ( this.saved == wordTrackers.size() ) {
                saved = 0;
                rotateOrder();
                return true;
            }
        }
        return false;
    }

    /**
     * Used to save a guess on a image.
     * @param id guesser ID
     * @param guess guess
     * @return boolean true if all guesses this round is saved.
     */
    public boolean saveGuess(int id, String guess ) {
        int index = playerOrder.indexOf(id);
        WordTracker tracker = wordTrackers.get(index);
        boolean success = tracker.saveGuess(id,guess);

        WordTracker.Entry latestEntry = tracker.getLatestEntry();
        if (success && latestEntry.isCorrect()) {
            gameSession.getConnectedPlayer(latestEntry.getImageSubmitterId()).givePoints(1);
            gameSession.getConnectedPlayer(latestEntry.getGuessSubmitterId()).givePoints(1);
        }

        if ( success ) {
            this.saved++;
            if ( this.saved == wordTrackers.size() ) {
                saved = 0;
                rotateOrder();
                return true;
            }
        }
        return false;
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
    public int getNumberOfWords(){ return wordTrackers.size(); }

    /**
     * Displays a RoundData as String.
     * @return String
     */
    public String toString() {
        StringBuilder words = new StringBuilder();
        for ( int i = 0; i < wordTrackers.size(); i++ ) {
            words.append(i).append(wordTrackers.get(i)).append("\n");
        }
        return words.toString();
    }

}
