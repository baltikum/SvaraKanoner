package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


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
    private int numWordsToPick;

    private Timer timer;

    public PickWordPhase(GameSession gameSession) {
        this.session = gameSession;
        settings = session.getGameSettings();
        AllWords allwords = new AllWords();


        int numPlayers = session.getConnectedPlayers().size();
        boolean unevenPlayerCount = numPlayers % 2 != 0;
        numWordsToPick = unevenPlayerCount ? numPlayers - 1 : numPlayers;

        ArrayList<String> allGenerateWords = allwords.getWords(numPlayers * settings.getNumberOfWords());

        for (int index = 0; index < numWordsToPick; index++) {
            ClientHandler client = session.getConnectedPlayers().get(index);
            String[] words = new String[settings.getNumberOfWords()];
            for (int i = 0; i < settings.getNumberOfWords(); i++) {
                words[i] = allGenerateWords.get(0);
                allGenerateWords.remove(0);
            }
            generatedWords.put(client.getId(), words);

            Message gotoMessage = new Message(Message.Type.GOTO);
            gotoMessage.addParameter("phase", "PickWordPhase");
            gotoMessage.addParameter("words", words);
            client.sendMessage(gotoMessage);
        }

        if (unevenPlayerCount) {
            Message gotoMsg = new Message(Message.Type.GOTO);
            gotoMsg.addParameter("phase", "WaitingPhase");
            session.getConnectedPlayers().get(numWordsToPick).sendMessage(gotoMsg);
        }

        timer = new Timer((int)settings.pickTimeMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Random random = new Random();
                // time is up
                List<ClientHandler> clients = session.getConnectedPlayers();
                for (int i = 0; i < numWordsToPick; i++) {
                    ClientHandler client = clients.get(i);
                    if (!pickedWords.containsKey(client.getId())) {
                        String randomWord = generatedWords.get(client.getId())[random.nextInt() % 4];
                        addPickedWords(client.getId(), randomWord);
                    }
                }
            }
        });
        timer.start();
    }

    private void addPickedWords(int id, String word) {
        pickedWords.put(id, word);
        if (pickedWords.size() == numWordsToPick) {
            enterDrawPhase();
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
                addPickedWords(msg.player.getId(), word);
            }
        }
    }

    private void enterDrawPhase() {
        timer.stop();
        session.createRoundData(pickedWords);
        session.setPhase(new DrawPhase(session));
    }
}
