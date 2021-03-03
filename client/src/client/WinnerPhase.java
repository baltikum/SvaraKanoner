package client;

import client.ui.*;
import common.Message;
import common.Phase;
import common.Player;

import javax.swing.*;
import java.awt.*;

public class WinnerPhase extends Phase {

    public WinnerPhase(Message gotoMessage) {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));

        int[] placements = (int[]) gotoMessage.data.getOrDefault("placements", null);
        int[] playerIds = (int[]) gotoMessage.data.getOrDefault("playerIds", null);
        int[] points = (int[]) gotoMessage.data.getOrDefault("points", null);

        if (placements != null && playerIds != null && points != null &&
            placements.length == playerIds.length && playerIds.length == points.length) {
            final int numPlayers = placements.length;
            float width = 1.0f / (float) numPlayers;
            float yAdvance = 0.2f / (float) numPlayers;
            float xOffset = width / 2.0f;
            for (int i = 0; i < numPlayers; i++) {
                addPlayerToPodium(panel, placements[i], playerIds[i], points[i],
                        xOffset + width * i, 0.2f + (placements[i] - 1) * yAdvance, width);
            }
        } else {
            AwesomeText allWinners = new AwesomeText("You are all winners!");
            panel.add(allWinners);
            layout.setConstraintsRatioByWidth(allWinners, 0.5f, 0.5f, 1.0f, 0.2f);
            AwesomeEffect.create()
                    .addRotationKey(20.0f, 1000)
                    .addRotationKey(-20.0f, 2000)
                    .addRotationKey(0.0f, 3000)
                    .repeats(AwesomeEffect.INFINITY).animate(allWinners, AwesomeEffect.COMPONENT);
        }

        AwesomeButton leave = new AwesomeButton("leave");
        leave.addActionListener( e -> Game.game.setCurrentPhase(new MainMenu()) );
        panel.add(leave);
        layout.setConstraintsRatioByWidth(leave, 0.5f, 0.9f, 0.2f, 0.25f);

        Game.game.getAudioPlayer().playEffect(AudioPlayer.CLAPS_EFFECT);
        Game.game.setContentPanel(panel);
    }

    private void addPlayerToPodium(JPanel panel, int placement, int playerId, int points, float x, float y, float width) {
        Player player = Game.game.getPlayer(playerId);
        if (player == null) return;

        AwesomeText placementLabel = new AwesomeText(placement + " (" + points + ")");
        AwesomeImage playerIcon = new AwesomeImage(Assets.getPlayerIcons()[player.getAvatarId()]);

        PercentLayout layout = (PercentLayout) panel.getLayout();
        panel.add(placementLabel);
        panel.add(playerIcon);
        layout.setConstraintsRatioByWidth(placementLabel, x, y, width, 0.5f);
        y += width * 0.5f + 0.1f;
        layout.setConstraintsRatioByWidth(playerIcon,     x, y, width, 1.0f);
    }

    @Override
    public void message(Message msg) {

    }

}
