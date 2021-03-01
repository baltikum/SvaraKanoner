package server;

import common.Pair;

import java.awt.Image;
import java.util.ArrayList;

/**
 * A Class that keeps track of one word.
 * The word, String. The guesses, Strings. The Images images.
 *
 *@author Mattias Davidsson 20210223
 */

public class WordTracker {

    private int wordOwnerId;
    private String word;
    private ArrayList<Pair> images;
    private ArrayList<Pair> guesses;


    /**
     * Constructor
     *
     * @param word
     */
    public WordTracker(int id, String word ){
        this.wordOwnerId = id;
        this.word = word;
        this.images = new ArrayList<>();
        this.guesses = new ArrayList<>();
    }

    /**
     * Saves a drawing into its Wordtracker as a Pair.
     * @param id personal id or artist.
     * @param image The image.
     * @return boolean
     */
    public boolean saveDrawing(int id, Image image) {
        int temp = images.size();
        this.images.add(new Pair(id,image));
        return (temp+1) == images.size();
    }
    /**
     * Saves a guess into its Wordtracker as a Pair.
     * @param id tagging the guess with a personal id
     * @param guess the guess
     * @return boolean
     */
    public boolean saveGuess(int id, String guess ) {
        int temp = guesses.size();
        this.guesses.add(new Pair(id,guess));
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
     * @param i , The index of the guess, see roundpartCount in RoundData.
     * @return Pair Playerid , String guess
     */
    public Pair getGuess(int i ) { return guesses.get(i); }

    /**
     * Used to retrieve all guesses made on this word throughout the round.
     * @return ArrayList of Pairs, playerID , guess
     */
    public ArrayList<Pair> getAllGuesses() { return guesses; }

    /**
     * Used to retrieve all images made for this word.
     * @return ArrayList of pairs. PlayerId, Images.
     */
    public ArrayList<Pair> getAllImages() { return images; }

    /**
     * Gets an image based on index. See roundCountData.
     * @param i index to get.
     * @return Pair id,Image
     */
    public Pair getDrawing(int i ) { return images.get(i); }

    /**
     * Return this WordTracker as a simple String for debugging.
     * @return string
     */
    public String toString() { return "Word: " + word + ".\nGuesses: " + guesses.size() + ".\nImages : " + images.size() + ".\n"; }

}
