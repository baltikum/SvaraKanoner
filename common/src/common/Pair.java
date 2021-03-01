package common;

import java.util.ArrayList;
import java.util.List;

public class Pair {
    private int id;
    private ArrayList<List<PaintPoint>> image;
    private String word;

    public Pair( int id, ArrayList<List<PaintPoint>> image ) {
        this.id = id;
        this.image = image;
    }
    public Pair( int id, String str ) {
        this.id = id;
        this.word = str;
    }

    public int getPlayerId() { return id; }
    public ArrayList<List<PaintPoint>> getImage(){ return image; }
    public String getGuess() { return word; }
}
