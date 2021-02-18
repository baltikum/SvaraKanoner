package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class JoinPhase extends Phase {
    private static final int NUM_POSITIONS = 16;
    private static final int POSITION_DATA_COMPONENTS = 3;


    private final Image[] playerIcons;
    private final float[] positionData;
    private final ArrayList<Integer> freePositions;
    private final JPanel panel;
    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();

    public JoinPhase() {
        Random random = Game.game.random;
        freePositions = new ArrayList<>(16);
        positionData = new float[NUM_POSITIONS * POSITION_DATA_COMPONENTS];

        float x = 0.0f, y = 0.167f;
        for (int i = 0; i < NUM_POSITIONS; i++) {
            if (i == 5) {
                x = 0.2f * 4;
                y = 0.167f * 2;
            } else if (i == 14) {
                x = 0.2f * 2;
                y = 0.167f * 5;
            } else {
                x += 0.2f;
                if (x > 0.95f) {
                    x = 0.2f;
                    y += 0.167f;
                }
            }
            positionData[i * POSITION_DATA_COMPONENTS] = x;
            positionData[i * POSITION_DATA_COMPONENTS + 1] = y;
            positionData[i * POSITION_DATA_COMPONENTS + 2] = (random.nextFloat() - 0.5f) * 90.0f;
            freePositions.add(i);
        }

        BufferedImage tileMap = Assets.loadImage("player-icons.png");
        playerIcons = Assets.getTiles(tileMap, 0, 0, 1, 1, 8, 4, 1, 16);

        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 0));

        AwesomeText gameCode = new AwesomeText(Game.game.getGameCode(), AwesomeUtil.BIG_TEXT);
        panel.add(gameCode);
        layout.setConstraintsRatioByWidth(gameCode, 0.5f, .167f * 2, .5f, 0.25f);

        AwesomeButton ready = new AwesomeButton("Ready!", AwesomeUtil.MEDIUM_TEXT);
        panel.add(ready);
        layout.setConstraintsRatioByWidth(ready, .75f, .167f * 5, .3f, 0.25f);

        ready.addActionListener(e -> {
            Game.game.sendMessage(new Message(Message.Type.TOGGLE_READY_STATUS));
        });

        AwesomeButton leave = new AwesomeButton("Leave", AwesomeUtil.MEDIUM_TEXT);
        panel.add(leave);
        layout.setConstraintsRatioByWidth(leave, .25f, .167f * 5, .3f, 0.25f);
        leave.addActionListener(e -> {
            Game.game.sendMessage(new Message(Message.Type.DISCONNECT));
            Game.game.setCurrentPhase(new MainMenu());
        });

        Game.game.setContentPanel(panel);
        addPlayer(Game.game.getThisPlayer());
    }

    public void addPlayer(Player player) {
        List<Player> players = Game.game.getPlayers();
        players.add(player);

        int positionIndex = players.size() < 8 ?
                freePositions.remove(Game.game.random.nextInt(8 - players.size())) :
                freePositions.remove(Game.game.random.nextInt(freePositions.size()));
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(playerIcons[player.getAvatarId()], player.getName());
        panel.add(playerLabel);
        ((PercentLayout)panel.getLayout()).setConstraintsRatioByWidth(playerLabel,
                positionData[positionIndex * POSITION_DATA_COMPONENTS],
                positionData[positionIndex * POSITION_DATA_COMPONENTS + 1],0.2f, 0.3f);
        playerIdToLabel.put(player.getId(), playerLabel);

        AwesomeEffect.create()
                .addScaleKey(0.0f, 0.0f, 0)
                .addScaleKey(1.2f, 1.2f, 700)
                .addScaleKey(1.0f , 1.0f, 1000)
                .addRotationKey(positionData[positionIndex * POSITION_DATA_COMPONENTS + 2], 0)
                .animate(playerLabel, AwesomeEffect.COMPONENT);
    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case PLAYER_CONNECTED -> {
                Player player = new Player(
                        (int) msg.data.get("playerId"),
                        (String) msg.data.get("playerName"),
                        (int) msg.data.get("playerAvatarId")
                );
                addPlayer(player);
            }
            case PLAYER_DISCONNECTED -> {
                int playerId = (int) msg.data.getOrDefault("playerId", -1);
                AwesomeIconLabel label = playerIdToLabel.get(playerId);
                if (label != null) panel.remove(label);
            }
            case PLAYER_READY_STATUS_CHANGED -> {
                int playerId = (int) msg.data.getOrDefault("playerId", -1);
                boolean status = (boolean) msg.data.getOrDefault("status", false);
                AwesomeIconLabel label = playerIdToLabel.get(playerId);
                if (label != null) label.setTextColor(status ? Color.GREEN : Color.BLACK);
            }
        }
    }

}
