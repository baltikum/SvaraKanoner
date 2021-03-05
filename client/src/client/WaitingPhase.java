package client;

import client.ui.*;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;


public class WaitingPhase implements Phase {
    private final JPanel panel;

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

        AwesomeButton rocketButton = new AwesomeButton("Go!", Assets.getMainmenuIcon(Assets.MENU_ROCKET));
        AwesomeImage rocketFlame = new AwesomeImage(Assets.getMainmenuIcon(Assets.MENU_FLAME0));
        rocketFlame.setVisible(false);

        rocketButton.addActionListener(e -> {
            AwesomeEffect.Builder builder = AwesomeEffect.create();
            builder .addTranslationXKey(panel.getWidth(), 1000)
                    .addTranslationXKey(-panel.getWidth(), 1001)
                    .addTranslationXKey(0, 2000).animate(rocketButton);
            for (int i = 0; i < 10; i++) {
                builder.addSpriteKey(Assets.getMainmenuIcon(Assets.MENU_FLAME0), i * 200);
                builder.addSpriteKey(Assets.getMainmenuIcon(Assets.MENU_FLAME1), i * 200 + 100);
            }
            builder.animate(rocketFlame);
            rocketFlame.setVisible(true);

        });

        panel.add(rocketButton);
        panel.add(rocketFlame);

        layout.setConstraintsRatioByWidth(rocketButton, 0.5f, 0.7f, .6f, .5f);
        layout.setConstraintsRatioByWidth(rocketFlame, 0.28f, 0.7f, .1f, 1.0f);

        Game.getInstance().setContentPanel(panel);
    }

    @Override
    public void message(Message msg) {


    }
}
