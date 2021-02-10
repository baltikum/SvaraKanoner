package server;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A Class that keeps track of a gamerounds data.
 *
 * ArrayList of words ( WordTrackers ).
 * The number of words in this round, int.
 *
 * Has functions to save drawn images and guesses.
 *
 * Also has functions to get the drawn images and guesses.
 *
 * baltikum 20210210
 */

public class RoundData {

    private int nbrWords;
    private List<WordTracker> wordTracker;


    public RoundData(ArrayList<String> pickedWords){
        int nbrWords = pickedWords.size();
        wordTracker = new ArrayList<WordTracker>();

        ListIterator<String> pickedIterator = pickedWords.listIterator();
        while ( pickedIterator.hasNext() ) {
            this.wordTracker.add( new WordTracker( pickedIterator.next() ) );
        }
    }

    public void saveDrawings( ArrayList<Image> images ) throws Exception {
        if ( images.size() == nbrWords ) {
            ListIterator<Image> imagesIterator = images.listIterator();
            ListIterator<WordTracker> wordIterator = wordTracker.listIterator();;
            while ( imagesIterator.hasNext() ) {
                wordIterator.next().saveDrawing(imagesIterator.next());
            }

        } else {
            throw new Exception("Number of drawn images does not match the amount of this round");
        }
    }
    public void saveGuesses( ArrayList<String> guesses ) throws Exception {
        if ( guesses.size() == nbrWords ) {
            ListIterator<String> guessesIterator = guesses.listIterator();
            ListIterator<WordTracker> wordIterator = wordTracker.listIterator();;
            while ( guessesIterator.hasNext() ) {
                wordIterator.next().saveGuess(guessesIterator.next());
            }
        } else {
            throw new Exception("Number of guesses does not match the amount of this round");
        }
    }

}
