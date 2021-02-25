package server;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * Class to manage the Words of the game.
 * 20210212 baltikum
 */

public class AllWords {
    private static File file;
    private static Random random;
    private static ArrayList<String> allWords;


    /**
     *
     * Constructor. Reads from file.
     * "server/words/englishWords.txt"
     */
    public AllWords(){

        this.allWords = new ArrayList<String>();
        this.file = new File("server/words/en.txt");
        this.random = new Random();

        try (
            InputStream inStream = new FileInputStream(file);
            InputStreamReader inStreamReader = new InputStreamReader(inStream);
            BufferedReader bufferedReader = new BufferedReader(inStreamReader);
            ) {

            String line;
            while((line = bufferedReader.readLine()) != null) {
                allWords.add(line);
            }
        } catch (Exception ex ){
            System.out.println("allWords not loaded" + ex );
        }
    }

    /**
     *
     * Function returns specified amount of random words.
     *
     * @param amount
     * @return gameWords
     */
    public  ArrayList<String> getWords(int amount) {
        ArrayList<String> gameWords = new ArrayList<>(amount);

        int index;
        for ( int i = 0; i<amount; i++ ) {
            index = random.nextInt(allWords.size()-1);
            gameWords.add(allWords.get(index));
            allWords.remove(index);
        }
        return gameWords;
    }

    /**
     *
     * For developers use only. Adding new words into file.
     * @param word
     * @return
     */
    public String addWordToDictionary(String word ) {
        String cleanWord = checkFixWord(word);
        if ( !checkForExistingWord(cleanWord) ) {
            try( BufferedWriter output =
                         new BufferedWriter(
                                 new FileWriter("server/words/englishWords.txt", true));
            ) {
                output.append(cleanWord + "\n");
                return "Successfully added word.\n";
            } catch (IOException ex){
                return "Failed to add word.\n";
            }
        }
        return "Word already exists in file.\n";
    }

    /**
     *
     * Checks if word already exists.
     * @param word
     * @return
     */
    private boolean checkForExistingWord(String word ) {
        if ( allWords.contains(word) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * Controls only alphabetical letters,trims deadspace and Capitalizes initial letter.
     * @param word
     * @return
     */
    private String checkFixWord( String word ) {
        if ( word == null ) {
            return "NULL";
        }
        String trimmed = word.trim();
        boolean checkCharacters = trimmed.matches(".*[A-Za-z].*");

        if ( trimmed == "" ) {
            return "Is empty.\n";
        } else if ( !checkCharacters ) {
            return "Only letters.\n";
        }

        String capital = (trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1));
        return capital;
    }
}
