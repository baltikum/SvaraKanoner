package common;



import java.io.*;

/**
 *
 * Class to hold all the settings of a hosted game.
 * 20210212 baltikum
 */

public class GameSettings implements Serializable {

    public long pickTimeMilliseconds;
    public long drawTimeMilliseconds;
    public long guessTimeMilliseconds;
    public long revealTimeMilliseconds;
    public long courtTimeMilliseconds;
    public boolean keepScore;
    public boolean chooseWords;
    public boolean shakyHands;
    public int maxPlayers;
    public int numRounds;
    public int numberOfWordsToPickFrom;

    /**
     * Constructor GameSettings.
     * Default settings.
     */
    public GameSettings(){
        this.pickTimeMilliseconds = 10L * 1000L;
        this.drawTimeMilliseconds = 60L * 1000L;
        this.guessTimeMilliseconds = 20L * 1000L;
        this.revealTimeMilliseconds = 8L * 1000L;
        this.courtTimeMilliseconds = 15L * 1000L;
        this.keepScore = true;
        this.chooseWords = false;
        this.shakyHands = false;
        this.maxPlayers = 4;
        this.numRounds = 5;
        this.numberOfWordsToPickFrom = 4;
    }

    /**
     * Set variable functions. Flipswitch.
     */
    public void toggleKeepScore() {
        keepScore = !keepScore;
    }
    public void toggleChooseWords() {
        chooseWords = !chooseWords;
    }
    public void toggleShakyHands() {
        shakyHands = !shakyHands;
    }

    /**
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
    public boolean setNumberOfWords(int amount ) {
        if ( amount >= 1 && amount < 60 ) {
            this.numberOfWordsToPickFrom = amount;
            return true;
        }
        return false;
    }

    /**
     * Get variable functions.
     * @return
     */
    public boolean getKeepScore(){ return keepScore; }
    public boolean getChooseWords(){ return chooseWords; }
    public boolean getShakyHands(){ return shakyHands; }
    public int getMaxPlayers(){ return maxPlayers; }
    public int getNumRounds(){ return numRounds; }
    public int getNumberOfWords() { return numberOfWordsToPickFrom; }
    public long getPickTimeMilliseconds(){ return pickTimeMilliseconds; }
    public long getDrawTimeMilliseconds(){ return drawTimeMilliseconds; }
    public long getGuessTimeMilliseconds(){ return guessTimeMilliseconds; }
    public long getRevealTimeMilliseconds(){ return revealTimeMilliseconds; }
    public long getCourtTimeMilliseconds(){ return courtTimeMilliseconds; }


    /**
     * Saves GameSettings on host.
     *
     * @return true if successful.
     *
     */
    public boolean saveSettingsIni() {
        try {
            IniStream.write(this, new File("server/settings/settings.ini"));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Loads GameSettings from host to game.
     * @return true if successful.
     *
     */
    public boolean loadSettingsIni() {
        GameSettings loadedSettings = new GameSettings();
        try {
            IniStream.read(loadedSettings, new File("server/settings/settings.ini"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        this.pickTimeMilliseconds = checkLongValue(loadedSettings.getPickTimeMilliseconds());
        this.drawTimeMilliseconds = checkLongValue(loadedSettings.getDrawTimeMilliseconds());
        this.guessTimeMilliseconds = checkLongValue(loadedSettings.getGuessTimeMilliseconds());
        this.revealTimeMilliseconds = checkLongValue(loadedSettings.getRevealTimeMilliseconds());
        this.courtTimeMilliseconds = checkLongValue(loadedSettings.getCourtTimeMilliseconds());
        this.keepScore = loadedSettings.getKeepScore();
        this.chooseWords = loadedSettings.getChooseWords();
        this.shakyHands = loadedSettings.getShakyHands();
        this.maxPlayers = checkIntValue(loadedSettings.getMaxPlayers());
        this.numRounds = checkIntValue(loadedSettings.getNumRounds());
        this.numberOfWordsToPickFrom = checkIntValue(loadedSettings.getNumberOfWords());
        return true;
    }

    /**
     * Controls validity of time values before loading them.
     * @param time
     * @return corrected value if needed
     */
    private long checkLongValue(long time ) {
        return Math.max(0L, Math.min(300000L, time));
    }
    /**
     * Controls validity of integer values before loading them.
     * @param value
     * @return corrected value if needed.
     */
    private int checkIntValue(int value ) {
        return Math.max(0, Math.min(300000, value));
    }


    /**
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
