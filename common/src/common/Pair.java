package common;

import java.awt.*;

public class Pair {
    private int id;
    private PaintPoint image;
    private String word;

    public Pair( int id, PaintPoint image ) {
        this.id = id;
        this.image = image;
    }
    public Pair( int id, String str ) {
        this.id = id;
        this.word = str;
    }

    public int getPlayerId() { return id; }
    public PaintPoint getImage(){ return image; }
    public String getGuess() { return word; }
}
