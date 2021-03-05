package client;

import client.ui.*;
import common.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represent the clients join phase, where the other players can be seen to appear and the game starts
 * when all are ready.
 *
 * Can send a TOGGLE_PLAYER_READY or LEAVE message.
 * Gets a message from the server when all are ready to goto the next phase PickWord or DrawPhase.
 */
public class JoinPhase implements Phase {
    private static final int NUM_POSITIONS = 16;
    private static final int POSITION_DATA_COMPONENTS = 3;

    private final GameSession session;
    private final Random random = new Random();
    private final float[] positionData;
    private final ArrayList<Integer> freePositions;
    private final JPanel panel;
    private final Map<Integer, AwesomeIconLabel> playerIdToLabel = new HashMap<>();


    /**
     * Initiates the join phase ui.
     */
    public JoinPhase() {
        Game game = Game.getInstance();
        session = game.getSession();

        game.getChat().setVisible(true);
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

        PercentLayout layout = new PercentLayout(1.0f);
        panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 0));

        AwesomeText gameCode = new AwesomeText(session.getSessionId());
        panel.add(gameCode);
        layout.setConstraintsRatioByWidth(gameCode, 0.5f, .167f * 2, .35f, 0.25f);
        AwesomeUtil.dynamicFont(gameCode, 1.0f);

        AwesomeButton leave = new AwesomeButton("Leave");
        AwesomeButton ready = new AwesomeButton("Ready!");
        panel.add(ready);
        panel.add(leave);
        layout.setConstraintsRatioByWidth(ready, .75f, .167f * 5, .3f, 0.25f);
        layout.setConstraintsRatioByWidth(leave, .25f, .167f * 5, .3f, 0.25f);
        ready.addActionListener(e -> {
            game.sendMessage(new Message(Message.Type.TOGGLE_READY_STATUS));
        });
        leave.addActionListener(e -> {
            game.sendMessage(new Message(Message.Type.DISCONNECT));
            game.leaveSession();
        });
        AwesomeUtil.dynamicFont(ready, 1.0f);
        AwesomeUtil.dynamicFont(leave, 1.0f);
        AwesomeUtil.wiggleOnHover(ready, 20.0f);
        AwesomeUtil.wiggleOnHover(leave, 20.0f);

        addPlayer(session.getThisPlayer());
        game.setContentPanel(panel);
    }

    public void addPlayer(Player player) {
        List<Player> players = session.getPlayers();
        players.add(player);
        session.getPhaseUI().addPlayerToList(player);

        int positionIndex = players.size() < 8 ?
                freePositions.remove(random.nextInt(8 - players.size())) :
                freePositions.remove(random.nextInt(freePositions.size()));
        AwesomeIconLabel playerLabel = new AwesomeIconLabel(Assets.getAvatarImage(player.getAvatarId()), player.getName());
        panel.add(playerLabel);
        ((PercentLayout)panel.getLayout()).setConstraintsRatioByWidth(playerLabel,
                positionData[positionIndex * POSITION_DATA_COMPONENTS],
                positionData[positionIndex * POSITION_DATA_COMPONENTS + 1],0.2f, 0.3f);
        playerIdToLabel.put(player.getId(), playerLabel);
        AwesomeUtil.dynamicFont(playerLabel, .5f);

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

                session.getPhaseUI().removePlayerFromList(session.getPlayerById(playerId));
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
