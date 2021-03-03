package server;

import common.PaintPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A Class that keeps track of one word.
 * The word, String. The guesses, Strings. The Images images.
 *
 *@author Mattias Davidsson 20210223
 */

public class WordTracker {

    public class Entry {

        private final int imageSubmitterId;
        private final ArrayList<List<PaintPoint>> image;
        private int guessSubmitterId;
        private String guess;
        private boolean isCorrect;

        /**
         * Constructors, Used to keep track of guesses and images.
         * @param playerId The id of the player submitting
         * @param image image made up by PairPoints.
         */
        public Entry(int playerId, ArrayList<List<PaintPoint>> image ) {
            this.imageSubmitterId = playerId;
            this.image = image;
        }

        /**
         * Constructors, Used to keep track of word/guesses and word/images.
         * @param playerId The id of the player submitting
         * @param submittedGuess .
         */
        public void submitGuess(int playerId, String submittedGuess) {
            guessSubmitterId = playerId;
            guess = submittedGuess;

            if (wrongAnswerSubmittedOnce) {
                isCorrect = false;
            } else {
                String guessLower = submittedGuess.trim().toLowerCase(Locale.ROOT);
                String correctAnswer = pickedWord.toLowerCase(Locale.ROOT);
                isCorrect = guessLower.equals(correctAnswer);
            }
        }

        /**
         * Gets, used to retrieve data from this class.
         * @return int,String and List of Lists of Pairs.
         */
        public int getImageSubmitterId() { return imageSubmitterId; }
        public int getGuessSubmitterId() { return guessSubmitterId; }
        public ArrayList<List<PaintPoint>> getImage(){ return image; }
        public String getGuess() { return guess; }
        public boolean isCorrect() { return isCorrect; }
    }

    private final int pickerId;
    private final String pickedWord;
    private final ArrayList<Entry> entries = new ArrayList<>();
    private boolean wrongAnswerSubmittedOnce = false;


    /**
     * Constructor
     * @param id The id of the picker of the word.
     * @param word The word that was picked.
     */
    public WordTracker(int id, String word ){
        pickerId = id;
        pickedWord = word;
    }

    /**
     * Saves a drawing into its Wordtracker as a Pair.
     * @param id personal id or artist.
     * @param image The image.
     * @return True if the drawing was added.
     */
    public boolean saveDrawing(int id, ArrayList<List<PaintPoint>> image) {
        Entry latestEntry = getLatestEntry();
        if (latestEntry == null || latestEntry.getGuess() != null) {
            entries.add(new Entry(id, image));
            return true;
        }
        return false;
    }

    /**
     * Saves a guess into its Wordtracker as a Pair.
     * @param id tagging the guess with a personal id
     * @param guess the guess
     * @return True if the guess was added.
     */
    public boolean saveGuess(int id, String guess) {
        Entry latestEntry = getLatestEntry();
        if (latestEntry.getGuess() == null) {
            latestEntry.submitGuess(id, guess);
            if (!latestEntry.isCorrect()) {
                wrongAnswerSubmittedOnce = true;
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the id of this words owner.( The one who chose it.)
     * @return integer id
     */
    public int getPickerId(){ return pickerId; }

    /**
     * Returns the word of this WordTracker.
     * @return The originally picked word.
     */
    public String getPickedWord() { return pickedWord; }

    /**
     * Used to retrieve the latest guess on a word.
     * @return String
     */
    public String getLatestGuess() {
        Entry latestEntry = getLatestEntry();
        return latestEntry == null ? pickedWord : latestEntry.getGuess();
    }

    /**
     * Used to retrieve the latest guess on a word.
     * @return String
     */
    public  ArrayList<List<PaintPoint>> getLatestImage() {
        return getLatestEntry().getImage();
    }

    /**
     * Used to retrieve the latest guess on a word.
     * @return String
     */
    public Entry getLatestEntry() {
        if (entries.isEmpty()) {
            return null;
        }
        return entries.get(entries.size() - 1);
    }

    public Entry getEntry(int index) {
        return entries.get(index);
    }

    /**
     * Return this WordTracker as a simple String for debugging.
     * @return string
     */
    public String toString() {
        return "Word: " + pickedWord + ".\nEntires: " + entries.size() + "\n";
    }

}
