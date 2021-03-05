package server;

import common.*;
import java.util.*;

/**
 * Keeps track of the winner phase responsibilities of the winner phase.
 *
 * @author Jesper Jansson
 * @version 04/03/21
 */
public class WinnerPhase implements Phase {

    /**
     * Constructs a winner phase.
     * Calculates the placement of all players and sends out the playerIds, placements, scores for each player.
     * If scores is disabled it only tells the players to go to the winner phase.
     * Then disconnects all players from the game session.
     * @param session The game session that should be finished.
     */
    public WinnerPhase(GameSession session) {
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
