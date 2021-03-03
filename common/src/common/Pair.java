package common;

import java.util.ArrayList;
import java.util.List;

public class Pair {

    private int id;
    private ArrayList<List<PaintPoint>> image;
    private String word;

    /**
     * Constructors, Used to keep track of word/guesses and word/images.
     * @param id
     * @param image,String, image made up by PairPoints.
     */
    public Pair( int id, ArrayList<List<PaintPoint>> image ) {
        this.id = id;
        this.image = image;
    }
    public Pair( int id, String str ) {
        this.id = id;
        this.word = str;
    }

    /**
     * Gets, used to retrieve data from this class.
     * @return int,String and List of Lists of Pairs.
     */
    public int getPlayerId() { return id; }
    public ArrayList<List<PaintPoint>> getImage(){ return image; }
    public String getGuess() { return word; }
}
