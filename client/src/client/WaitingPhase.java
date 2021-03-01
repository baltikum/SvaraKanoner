package client;

import client.ui.AwesomeIconLabel;
import client.ui.AwesomeText;
import client.ui.PercentLayout;
import common.Message;
import common.Phase;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WaitingPhase extends Phase {
    private final JPanel panel;

    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();

    public WaitingPhase() {
        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(23, 0, 0, 0));


        AwesomeText waitingMessage1 = new AwesomeText("You pass this turn..");
        AwesomeText waitingMessage2 = new AwesomeText("Please wait");

        waitingMessage1.setTextColor(Color.black);
        waitingMessage2.setTextColor(Color.black);

        layout.setConstraintsRatioByWidth(waitingMessage1, .5f, .4f, .8f, 0.25f);
        layout.setConstraintsRatioByWidth(waitingMessage2, .5f, .6f, .8f, 0.25f);
        panel.add(waitingMessage1);
        panel.add(waitingMessage2);
        Game.game.setContentPanel(panel);
    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case GOTO_DRAW_PHASE -> {
                Game.game.setCurrentPhase(new DrawPhase());
            }
            case GOTO_REVEAL_PHASE -> {
                Game.game.setCurrentPhase(new RevealPhase());
            }
        }
    }
}
