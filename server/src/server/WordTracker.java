package server;

import java.awt.*;

public class WordTracker {

    private String word;
    private String guess;
    private Image image;

    public WordTracker(){
        this.word = randomWord();
        this.image = null;
        this.guess = null;
    }

    public void saveDrawing(Image image) { this.image = image; }
    public void saveGuess(String guess ) { this.guess = guess; }
    private String randomWord() { this.word = "snopp"; }
}
