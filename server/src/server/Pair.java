package server;

import java.awt.*;

public class Pair {
    private int id;
    private Image image;
    private String word;

    public Pair( int id, Image image ) {
        this.id = id;
        this.image = image;
    }
    public Pair( int id, String str ) {
        this.id = id;
        this.word = str;
    }

    public int getPlayerId() { return id; }
    public Image getImage(){ return image; }
    public String getGuess() { return word; }
}
