package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import java.io.Console;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.ArrayList;

public class PickWordPhase extends Phase {
    private ArrayList<ClientHandler> clients;

    private GameSession session;

    private GameSettings settings;

    private Map<Integer, String[]> generatedWords = new HashMap<>();

    public PickWordPhase(GameSession gameSession) {
        this.session = gameSession;
        settings = session.getGameSettings();
        AllWords allwords = new AllWords();
        //System.out.println(allwords.getWords(10).toString());


        ArrayList<String> allGenerateWords = allwords.getWords(session.getConnectedPlayers().size()*settings.getNumberOfWords());

        for (ClientHandler client : session.getConnectedPlayers()) {
            String[] words = new String[settings.getNumberOfWords()];
            for (int i = 0; i < settings.getNumberOfWords(); i++) {
                words[i] = allGenerateWords.get(0);
                allGenerateWords.remove(0);
            }
            generatedWords.put(client.getId(), words);

        }







        // join phase -> message go to phase pick word phase
        // ändra phase på servern också
        // i server join word phase.
        // skicka alla ord som spelarna kan välja. bättre att spelare skickar till servern och frågar efter orden

        //SEND_WORD_CHOICES

        for (ClientHandler client: session.getConnectedPlayers()) {
            Message message = new Message(Message.Type.SEND_WORD_CHOICES);
            message.addParameter("words", generatedWords.get(client.getId()));
            client.sendMessage(message);
        }



    }








    @Override
    public void message(Message msg) {
        switch (msg.type) {
            /*
            case GET_WORD_CHOICES -> { // send back the word choices
                ClientHandler client = (ClientHandler) msg.player;
                Message message = new Message(Message.Type.RESPONSE);
                message.addParameter("words", generatedWords.get(client.getId()));
                client.sendMessage(message);

            }*/
            case PICK_WORD -> {
                int wordIndex = (int)msg.data.get("wordIndex");
                if (wordIndex < 0 || wordIndex > settings.getNumberOfWords()-1)
                    return;
                String word = generatedWords.get(msg.player.getId())[wordIndex];
                System.out.println("Picked word: " + word);
            }
        }
    }


}
