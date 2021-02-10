package server;

import java.awt.Image;

/**
 * A Class that keeps track of one word.
 * The word,String. The guess, String. The Image image.
 *
 *
 * baltikum 20210210
 */

public class WordTracker {

    private String word;
    private String guess;
    private Image image;

    /**
     * Constructor
     *
     * @param word
     */
    public WordTracker(String word){
        this.word = word;
        this.image = null;
        this.guess = null;
    }
    
    public void saveDrawing(Image image) { this.image = image; }
    public void saveGuess(String guess ) { this.guess = guess; }
    public String getWord() { return word; }
    public String getGuess() { return guess; }
    public Image getDrawing() { return image; }
    public String toString() { return "Word: " + word + ". Guess: " + guess + ". Image : " + image + ".\n"; }

}
