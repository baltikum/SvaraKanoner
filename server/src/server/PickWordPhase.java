package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;


/**
 * Server side of the pick word phase
 *
 * sends out 4 options of words to later draw to each connected client
 *
 * @author Lukas Magnusson
 */
public class PickWordPhase extends Phase {




    private GameSession session;

    private GameSettings settings;

    private Map<Integer, String[]> generatedWords = new HashMap<>();

    private HashMap<Integer,String> pickedWords = new HashMap<>();

    public PickWordPhase(GameSession gameSession) {
        this.session = gameSession;
        settings = session.getGameSettings();
        AllWords allwords = new AllWords();


        ArrayList<String> allGenerateWords = allwords.getWords(session.getConnectedPlayers().size()*settings.getNumberOfWords());

        for (ClientHandler client : session.getConnectedPlayers()) {
            String[] words = new String[settings.getNumberOfWords()];
            for (int i = 0; i < settings.getNumberOfWords(); i++) {
                words[i] = allGenerateWords.get(0);
                allGenerateWords.remove(0);
            }
            generatedWords.put(client.getId(), words);

        }



        for (ClientHandler client: session.getConnectedPlayers()) {
            Message message = new Message(Message.Type.SEND_WORD_CHOICES);
            message.addParameter("words", generatedWords.get(client.getId()));
            client.sendMessage(message);
        }



    }








    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case PICK_WORD -> {
                int wordIndex = (int)msg.data.get("wordIndex");
                if (wordIndex < 0 || wordIndex > settings.getNumberOfWords()-1)
                    return;
                String word = generatedWords.get(msg.player.getId())[wordIndex];
                System.out.println("Picked word: " + word);
                pickedWords.put(msg.player.getId(), word);

                if (pickedWords.size() == session.getConnectedPlayers().size()) {
                    enterDrawPhase();
                }

            }
        }
    }

    private void enterDrawPhase() {
        session.createRoundData(pickedWords);

        // TODO send go to draw phase msg to all players, create new drawPhase send over phase UI + session
    }



}
