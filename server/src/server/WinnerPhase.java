package server;

import common.Message;
import common.Phase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WinnerPhase extends Phase {

    private final GameSession session;

    public WinnerPhase(GameSession session) {
        this.session = session;

        Message msg = new Message(Message.Type.GOTO);
        msg.addParameter("phase", "WinnerPhase");
        if (session.getGameSettings().keepScore) {
            List<ClientHandler> players = new ArrayList<>(session.getConnectedPlayers());
            players.sort((c1, c2) -> (c2.getPoints() - c1.getPoints()));

            int[] placements = new int[players.size()];
            int[] playerIds = new int[players.size()];
            int[] points = new int[players.size()];

            int placement = 1;
            int previousScore = -1;
            for (int i = 0; i < players.size(); i++) {
                ClientHandler player = players.get(i);
                if (player.getPoints() != previousScore) {
                    placement = i + 1;
                    previousScore = player.getPoints();
                }

                System.out.println(player.getName() + " " + player.getPoints());
                playerIds[i] = player.getId();
                points[i] = player.getPoints();
                placements[i] = placement;
            }

            msg.addParameter("placements", placements);
            msg.addParameter("playerIds", playerIds);
            msg.addParameter("points", points);
        }
        session.sendMessageToAll(msg);
        session.terminate();
    }

    @Override
    public void message(Message msg) {

    }
}
