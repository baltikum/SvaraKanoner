package client;

import client.ui.*;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class WaitingPhase extends Phase {
    private final JPanel panel;

    private Image rocket,flame0,flame1;

    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();

    public WaitingPhase(Message msg) {
        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(23, 0, 0, 0));


        AwesomeText waitingMessage1 = new AwesomeText("You pass this turn..");
        AwesomeText waitingMessage2 = new AwesomeText("Please wait");

        waitingMessage1.setTextColor(Color.BLUE);
        waitingMessage2.setTextColor(Color.BLUE);

        layout.setConstraintsRatioByWidth(waitingMessage1, .5f, .3f, .8f, 0.25f);
        layout.setConstraintsRatioByWidth(waitingMessage2, .5f, .4f, .8f, 0.25f);
        panel.add(waitingMessage1);
        panel.add(waitingMessage2);

        BufferedImage tileMap = Assets.loadImage("mainmenu.png");
        rocket = Assets.getTile(tileMap, 0, 1, 6, 2, 8);
        flame0 = Assets.getTile(tileMap, 0, 3, 1, 1, 8);
        flame1 = Assets.getTile(tileMap, 1, 3, 1, 1, 8);

        AwesomeButton rocketButton = new AwesomeButton("Go!",rocket);
        AwesomeImage rocketFlame = new AwesomeImage(flame0);
        rocketFlame.setVisible(false);

        rocketButton.addActionListener(e -> {
            AwesomeEffect.Builder builder = AwesomeEffect.create();
            builder .addTranslationXKey(panel.getWidth(), 1000)
                    .addTranslationXKey(-panel.getWidth(), 1001)
                    .addTranslationXKey(0, 2000).animate(rocketButton);
            for (int i = 0; i < 10; i++) {
                builder.addSpriteKey(flame0, i * 200);
                builder.addSpriteKey(flame1, i * 200 + 100);
            }
            builder.animate(rocketFlame);
            rocketFlame.setVisible(true);

        });

        panel.add(rocketButton);
        panel.add(rocketFlame);

        layout.setConstraintsRatioByWidth(rocketButton, 0.5f, 0.7f, .6f, .5f);
        layout.setConstraintsRatioByWidth(rocketFlame, 0.28f, 0.7f, .1f, 1.0f);


        Game.game.setContentPanel(panel);
    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case GOTO -> {
                String str = (String)msg.data.get("phase");
                switch ( str ) {
                    case "DrawPhase":
                        Game.game.setCurrentPhase(new DrawPhase(msg));
                    case "RevealPhase":
                        Game.game.setCurrentPhase(new RevealPhase(msg));
                    case "GuessPhase":
                        Game.game.setCurrentPhase(new GuessPhase(msg));
                }
            }

        }
    }
}
