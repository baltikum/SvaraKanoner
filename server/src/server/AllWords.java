package server;

import java.io.*;
import java.util.*;

/**
 *
 * Class to manage the Words of the game.
 * Can genereate words to a game session. Can read words form textfile.
 *
 * @author Mattias Davidsson
 * @version 20210301
 */

public class AllWords {
    private static File file;
    private static Random random;
    private static ArrayList<String> allWordsInGame;
    private static HashSet<String> allWordsNoDuplicates;

    /**
     *
     * Constructor. Reads from file.
     * "server/words/en.txt"
     */
    public AllWords(){

        this.allWordsInGame = new ArrayList<>(700);
        this.file = new File("server/words/en.txt");
        this.random = new Random();
        this.allWordsNoDuplicates = new HashSet<String>(700);
        
        if ( loadWordsFromFile(file) ) {
            System.out.println("Words loaded.");
        }

    }

    /**
     * Used to load words from file into a ArrayList via HashSet.
     * 
     * @param file
     * @return boolean
     */
    public boolean loadWordsFromFile(File file) {
        try (
                InputStream inStream = new FileInputStream(file);
                InputStreamReader inStreamReader = new InputStreamReader(inStream);
                BufferedReader bufferedReader = new BufferedReader(inStreamReader);
        ) {

            String line;
            while((line = bufferedReader.readLine()) != null) {
                    allWordsNoDuplicates.add(fixWord(line));
            }

            for ( String str : allWordsNoDuplicates ) {
                allWordsInGame.add(str);
            }
            
        } catch (Exception ex ){
            System.out.println("All words not loaded" + ex );
        }
        return true;
    }

    /**
     *
     * Function returns specified amount of random words.
     *
     * @param amount
     * @return gameWords ArrayList
     */
    public  ArrayList<String> getWords(int amount) {
        ArrayList<String> gameWords = new ArrayList<>(amount);

        int index;
        for ( int i = 0; i<amount; i++ ) {
            index = random.nextInt(allWordsInGame.size()-1);
            gameWords.add(allWordsInGame.remove(index));
        }
        return gameWords;
    }

    /**
     *
     * For developers use only. Adding new words into the game.
     * @param word
     * @return boolean
     */
    public boolean addWordToDictionary(String word ) {
        if ( word.matches(".*[A-Za-z].*")) {
            String cleanWord = fixWord(word);
                try( BufferedWriter output =
                         new BufferedWriter(
                                 new FileWriter("server/words/en.txt", true));
                ) {
                    output.append(cleanWord + "\n");
                    System.out.println("Successfully added word.\n");
                    return true;
                } catch (IOException ex){
                    System.out.println("Failed to add word.\n");
                    return false;
                }
        }
        System.out.println("Word already exists in file.\n");
        return false;
    }
    
    /**
     *
     * Trims deadspace and sets lower upper case letters.
     * @param word
     * @return fixed String.
     */
    private String fixWord( String word ) {
        String trimmed = word.trim();
        String capital = (trimmed.substring(0, 1).toUpperCase(Locale.ROOT) + trimmed.substring(1).toLowerCase(Locale.ROOT));
        return capital;
    }
    
}
