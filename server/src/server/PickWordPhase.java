package server;

import common.GameSettings;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Random;


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

    private Timer timer;

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
            Message gotoMessage = new Message(Message.Type.GOTO);
            gotoMessage.addParameter("phase", "PickWordPhase");
            gotoMessage.addParameter("words", generatedWords.get(client.getId()));
            client.sendMessage(gotoMessage);
        }

        timer = new Timer((int)settings.pickTimeMilliseconds, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Random random = new Random();
                // time is up
                for (ClientHandler client: session.getConnectedPlayers()) {
                    if (!pickedWords.containsKey(client.getId())) {
                        String randomWord = generatedWords.get(client.getId())[random.nextInt()%4];
                        addPickedWords(client.getId(), randomWord);
                    }
                }
            }
        });



    }



    private void addPickedWords(int id, String word) {
        pickedWords.put(id, word);
        System.out.println("num submits = " + pickedWords.size());
        System.out.println("num players = " + session.getConnectedPlayers().size());
        if (pickedWords.size() == session.getConnectedPlayers().size()) {
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
        session.createRoundData(pickedWords);
        session.setPhase(new DrawPhase(session));
    }



}
