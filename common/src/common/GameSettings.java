package common;

import java.io.*;

/**
 *
 * Class to hold all the settings of a hosted game.
 * 20210212 baltikum
 */

public class GameSettings implements Serializable {

    private long pickTimeMilliseconds;
    private long drawTimeMilliseconds;
    private long guessTimeMilliseconds;
    private long revealTimeMilliseconds;
    private long courtTimeMilliseconds;
    private boolean keepScore;
    private boolean chooseWords;
    private boolean shakyHands;
    private int maxPlayers;
    private int numRounds;

    /**
     *
     * Constructor GameSettings.
     * Default settings.
     */
    public GameSettings(){
        this.pickTimeMilliseconds = 5L * 1000L;
        this.drawTimeMilliseconds = 60L * 1000L;
        this.guessTimeMilliseconds = 5L * 1000L;
        this.revealTimeMilliseconds = 8L * 1000L;
        this.courtTimeMilliseconds = 15L * 1000L;
        this.keepScore = true;
        this.chooseWords = false;
        this.shakyHands = false;
        this.maxPlayers = 4;
        this.numRounds = 5;
    }

    /**
     *
     * Set variable functions. Flipswitch.
     */
    public void setKeepScore() {
        if ( keepScore ) {
            keepScore = false;
        } else {
            keepScore = true;
        }
    }
    public void setChooseWords() {
        if ( chooseWords ) {
            chooseWords = false;
        } else {
            chooseWords = true;
        }
    }
    public void setShakyHands() {
        if ( shakyHands ) {
            shakyHands = false;
        } else {
            shakyHands = true;
        }
    }

    /**
     *
     * Set variable functions.
     * Returns true if value is set.
     * @param max,rounds,seconds.
     * @return
     */
    public boolean setMaxPlayers(int max) {
        if ( (max > 1) && (max < 30) ) {
            this.maxPlayers = max;
            return true;
        } else {
            return false;
        }
    }
    public boolean setRounds(int rounds ) {
        if ( (rounds > 1) && (rounds < 100)) {
            this.numRounds = rounds;
            return true;
        } else {
            return false;
        }
    }
    public boolean setPickTime(int seconds ){
        if ( (seconds>0) && (seconds<300) ) {
            long longSeconds = (long)seconds*1000L;
            this.pickTimeMilliseconds = longSeconds;
            return true;
        } else {
            return false;
        }
    }
    public boolean setDrawTime(int seconds ){
        if ( (seconds>0) && (seconds<300) ) {
            long longSeconds = (long)seconds*1000L;
            this.drawTimeMilliseconds = longSeconds;
            return true;
        } else {
            return false;
        }
    }
    public boolean setGuessTime(int seconds ){
        if ( (seconds>0) && (seconds<300) ) {
            long longSeconds = (long)seconds*1000L;
            this.guessTimeMilliseconds = longSeconds;
            return true;
        } else {
            return false;
        }
    }
    public boolean setRevealTime(int seconds ){
        if ( (seconds>0) && (seconds<300) ) {
            long longSeconds = (long)seconds*1000L;
            this.revealTimeMilliseconds = longSeconds;
            return true;
        } else {
            return false;
        }
    }
    public boolean setCourtTime(int seconds ){
        if ( (seconds>0) && (seconds<300) ) {
            long longSeconds = (long)seconds*1000L;
            this.courtTimeMilliseconds = longSeconds;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * Get variable functions.
     * @return
     */
    public boolean getKeepScore(){ return keepScore; }
    public boolean getChooseWords(){ return keepScore; }
    public boolean getShakyHands(){ return keepScore; }
    public int getMaxPlayers(){ return maxPlayers; }
    public int getNumRounds(){ return numRounds; }
    public long getPickTimeMilliseconds(){ return pickTimeMilliseconds; }
    public long getDrawTimeMilliseconds(){ return drawTimeMilliseconds; }
    public long getGuessTimeMilliseconds(){ return guessTimeMilliseconds; }
    public long getRevealTimeMilliseconds(){ return revealTimeMilliseconds; }
    public long getCourtTimeMilliseconds(){ return courtTimeMilliseconds; }

    /**
     *
     * Saves GameSettings on host.
     *
     * @return true if successful.
     * @throws IOException
     */
    public boolean saveSettings() throws IOException {
        try {
            FileOutputStream fileOutStream = new FileOutputStream("server/settings/settings.txt");
            ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
            objOutStream.writeObject(this);
            objOutStream.close();
        } catch(IOException ex) {
            return false;
        }
        return true;
    }

    /**
     *
     * Loads GameSettings from host to game.
     * @return true if successful.
     * @throws IOException
     */
    public boolean loadSettings() throws IOException {
        GameSettings loadedSettings;
        try {
            FileInputStream fileInStream = new FileInputStream("server/settings/settings.txt");
            ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
            loadedSettings = (GameSettings)objInStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return false;
        }

        this.pickTimeMilliseconds = loadedSettings.getPickTimeMilliseconds();
        this.drawTimeMilliseconds = loadedSettings.getDrawTimeMilliseconds();
        this.guessTimeMilliseconds = loadedSettings.getGuessTimeMilliseconds();
        this.revealTimeMilliseconds = loadedSettings.getRevealTimeMilliseconds();
        this.courtTimeMilliseconds = loadedSettings.getCourtTimeMilliseconds();
        this.keepScore = loadedSettings.getKeepScore();
        this.chooseWords = loadedSettings.getChooseWords();
        this.shakyHands = loadedSettings.getShakyHands();
        this.maxPlayers = loadedSettings.getMaxPlayers();
        this.numRounds = loadedSettings.getNumRounds();
        return true;
    }

    /**
     *
     * Displays the settings of this game.
     * @return String
     */
    public String toString(){
        return "Keeping score: " + keepScore + "\n" +
                "Number of Rounds: " + numRounds + "\n" +
                "Max Players:: " + maxPlayers + "\n" +
                "Pick word time: " + pickTimeMilliseconds + "\n" +
                "Draw time: " + drawTimeMilliseconds + "\n" +
                "Guess time: " + guessTimeMilliseconds + "\n" +
                "Reveal time: " + revealTimeMilliseconds + "\n" +
                "Court time: " + courtTimeMilliseconds + "\n";

    }


}
