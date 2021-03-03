package server;

import common.Message;
import common.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the server side of the join phase waiting for players to join.
 * Will send a message to all players to goto the next phase when all players are ready.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class JoinPhase extends Phase {

    private final GameSession session;
    private int nextPlayerId = 1;
    private int takenAvatarIds = 0; // Bit 0 represent avatar id 0 and bit 1 id 1 and so on...
    private final List<ClientHandler> readyPlayers = new ArrayList<>();

    public JoinPhase(GameSession session){
        this.session = session;
    }

    /**
     * Tells all existing clients that a player has joined and adds this client to the session.
     * @param client The client to join the session.
     */
    public void addClient(ClientHandler client) {
        // Resolve potential conflicts
        client.setName(resolvePlayerName(client.getName()));
        client.setId(nextPlayerId++);
        if ((takenAvatarIds & (0x1 << client.getAvatarId())) != 0) {
            int avatarId = 0;
            while ((takenAvatarIds & (0x1 << avatarId)) != 0)
                ++avatarId;
            client.setAvatarId(avatarId);
            takenAvatarIds |= 0x1 << client.getAvatarId();
        }

        List<ClientHandler> connectedClients = session.getConnectedPlayers();

        // Tell everyone that is already connected.
        Message playerConnectedMsg = new Message(Message.Type.PLAYER_CONNECTED);
        playerConnectedMsg.addParameter("playerName", client.getName());
        playerConnectedMsg.addParameter("playerId", client.getId());
        playerConnectedMsg.addParameter("playerAvatarId", client.getAvatarId());
        session.sendMessageToAll(playerConnectedMsg);

        // Finally add the client.
        connectedClients.add(client);
        client.setGameSession(session);
    }

    /**
     * Tells all remaining clients that a player has disconnected, and removes the client from the session.
     * @param client The client to disconnect.
     */
    public void disconnectClient(ClientHandler client) {
        if (session.getConnectedPlayers().remove(client)) {
            takenAvatarIds &= ~(0x1 << client.getAvatarId()); // Avatar is no longer taken.
            for (ClientHandler c : session.getConnectedPlayers()) {
                Message msg = new Message(Message.Type.PLAYER_DISCONNECTED);
                msg.addParameter("playerId", client.getId());
                c.sendMessage(msg);
                client.setGameSession(null);
            }
        }
    }

    /**
     * Listens for JOIN_GAME, DISCONNECT or TOGGLE_READY_STATUS messages.
     * @param msg The message.
     */
    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case JOIN_GAME -> {
                ClientHandler joiner = (ClientHandler) msg.player;
                Message response = new Message(Message.Type.RESPONSE);

                if (session.getConnectedPlayers().size() >= session.getGameSettings().getMaxPlayers()) {
                    response.error = "The server is full. ";
                } else {
                    joiner.setName((String) msg.data.getOrDefault("requestedName", ""));
                    joiner.setAvatarId((int) msg.data.getOrDefault("requestedAvatarId", 0));

                    // Add the existing players to the response message before adding the new player
                    List<ClientHandler> existingPlayers = session.getConnectedPlayers();
                    int[] existingPlayerIds = new int[existingPlayers.size()];
                    String[] existingPlayerNames = new String[existingPlayers.size()];
                    int[] existingPlayerAvatarIds = new int[existingPlayers.size()];
                    for (int i = 0; i < existingPlayers.size(); i++) {
                        ClientHandler player = existingPlayers.get(i);
                        existingPlayerIds[i] = player.getId();
                        existingPlayerAvatarIds[i] = player.getAvatarId();
                        existingPlayerNames[i] = player.getName();
                    }
                    response.addParameter("existingPlayerIds", existingPlayerIds);
                    response.addParameter("existingPlayerNames", existingPlayerNames);
                    response.addParameter("existingPlayerAvatarIds", existingPlayerAvatarIds);

                    addClient(joiner);
                    response.addParameter("sessionId", session.sessionID);
                    response.addParameter("playerAvatarId", joiner.getAvatarId());
                    response.addParameter("playerName", joiner.getName());
                    response.addParameter("playerId", joiner.getId());
                    response.addParameter("gameSettings", session.getGameSettings());
                }

                joiner.sendMessage(response);
            }
            case DISCONNECT -> {
                disconnectClient((ClientHandler) msg.player);
                if (session.getConnectedPlayers().isEmpty()) {
                    Main.removeSession(session);
                }
            }
            case TOGGLE_READY_STATUS -> {
                Message response = new Message(Message.Type.PLAYER_READY_STATUS_CHANGED);
                ClientHandler player = (ClientHandler) msg.player;
                response.addParameter("playerId", player.getId());
                if (readyPlayers.remove(player)) {
                    response.addParameter("status", false);
                    session.sendMessageToAll(response);
                } else {
                    readyPlayers.add(player);
                    response.addParameter("status", true);

                    if (readyPlayers.size() == session.getConnectedPlayers().size() &&
                            readyPlayers.size() >= 2) {
                        session.setPhase(new PickWordPhase(session));
                        Main.removeSession(session);
                    } else {
                        session.sendMessageToAll(response);
                    }
                }
            }
        }
    }


    private static final String[] defaultNames = { "Emily", "Courtney", "Alexander", "James", "Tyler" };
    /**
     * Checks that the given name is valid and not taken.
     * @param requestedName
     * @return
     */
    private String resolvePlayerName(String requestedName) {
        requestedName = requestedName.trim();
        if (requestedName.isEmpty()) {
            Random random = new Random();
            requestedName = defaultNames[random.nextInt(defaultNames.length)];
        } else if (requestedName.length() > 20) {
            requestedName = requestedName.substring(0, 20);
        }
        int i = 1;
        String givenName = requestedName;
        while (doesNameExist(givenName)) {
            givenName = requestedName + "#" + i;
        }
        return givenName;
    }

    /**
     *
     * @param name The name to check if is taken.
     * @return true if someone already has the name, else false.
     */
    private boolean doesNameExist(String name) {
        List<ClientHandler> connectedClients = session.getConnectedPlayers();
        for (ClientHandler c : connectedClients) {
            if (c.getName().equals(name))
                return true;
        }
        return false;
    }

}
