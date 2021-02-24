package server;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * A Class that keeps track of one word.
 * The word, String. The guesses, Strings. The Images images.
 *
 *@author Mattias Davidsson 20210223
 */

public class WordTracker {

    private int wordOwnerId;
    private String word;
    private HashMap<Integer,Image> images;
    private HashMap<Integer, String> guesses;


    /**
     * Constructor
     *
     * @param word
     */
    public WordTracker(int id, String word ){
        this.wordOwnerId = id;
        this.word = word;
    }

    /**
     * Saves a drawing into its Wordtracker.
     * @param id personal id or artist.
     * @param image The image.
     * @return boolean
     */
    public boolean saveDrawing(int id, Image image) {
        int temp = images.size();
        this.images.put(id,image);
        return (temp+1) == images.size();
    }
    /**
     * Saves a guess into its Wordtracker.
     * @param id tagging the guess with a personal id
     * @param guess the guess
     * @return boolean
     */
    public boolean saveGuess(int id, String guess ) {
        int temp = guesses.size();
        this.guesses.put(id,guess);
        return (temp+1) == images.size();
    }

    /**
     * Returns the id of this words owner.( The one who chose it.)
     * @return integer id
     */
    public int getWordOwnerId(){ return wordOwnerId; }
    /**
     * Returns the word of this WordTracker.
     * @return word
     */
    public String getWord() { return word; }
    /**
     * Gets one guess from the WordTracker based on id.
     * @param id , The id of who made the guess
     * @return
     * guess, string
     */
    public String getGuess(int id ) { return guesses.get(id); }

    /**
     * Used to retrieve all guesses made on this word throughout the round.
     * @return HashMap, playerID maps guess
     */
    public HashMap<Integer,String> getAllGuesses() { return guesses; }

    /**
     * Used to retrieve all images made for this word.
     * @return HashMap, PlayerId maps Images.
     */
    public HashMap<Integer,Image> getAllImages() { return images; }

    /**
     * Gets an image based on which id. Example ownId -1 is the one to guess on.(Except if your id 0)
     * @param id
     * @return image
     */
    public Image getDrawing(int id ) { return images.get(id); }

    /**
     * Return this WordTracker as a simple String for debugging.
     * @return string
     */
    public String toString() { return "Word: " + word + ".\nGuesses: " + guesses.size() + ".\nImages : " + images.size() + ".\n"; }

}
