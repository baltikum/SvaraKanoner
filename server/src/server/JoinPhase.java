package server;

import common.Message;
import common.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JoinPhase extends Phase {

    private final GameSession session;
    private int nextPlayerId = 1;
    private int takenAvatarIds = 0; // Bit 0 represent avatar id 0 and bit 1 id 1 and so on...
    private final List<ClientHandler> readyPlayers = new ArrayList<>();

    public JoinPhase(GameSession session){
        this.session = session;
    }

    public void addClient(ClientHandler client) {
        // Resolve potential conflicts
        client.setName(resolvePlayerName(client.getName()));
        client.setId(nextPlayerId++);
        if ((takenAvatarIds & (0x1 << client.getAvatarId())) != 0) {
            int avatarId = 0;
            while ((takenAvatarIds & (0x1 << avatarId)) != 0)
                ++avatarId;
            client.setAvatarId(avatarId);
        }
        takenAvatarIds |= 0x1 << client.getAvatarId();

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

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case JOIN_GAME -> {
                ClientHandler joiner = (ClientHandler) msg.player;
                Message response = new Message(Message.Type.RESPONSE);
                joiner.setName((String) msg.data.getOrDefault("requestedName", ""));
                joiner.setAvatarId((int) msg.data.getOrDefault("requestAvatarId", 0));

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
                joiner.sendMessage(response);
            }
            case DISCONNECT -> {
                disconnectClient((ClientHandler) msg.player);
            }
            case TOGGLE_READY_STATUS -> {
                Message response = new Message(Message.Type.PLAYER_READY_STATUS_CHANGED);
                ClientHandler player = (ClientHandler) msg.player;
                response.addParameter("playerId", player.getId());
                if (readyPlayers.remove(player)) {
                    response.addParameter("status", false);
                    session.sendMessageToAll(response);
                } else {
                    if (readyPlayers.size() == session.getConnectedPlayers().size()) {
                        // TODO: Goto next phase
                    } else {
                        readyPlayers.add(player);
                        response.addParameter("status", true);
                        session.sendMessageToAll(response);
                    }
                }
            }
        }
    }

    private static final String[] defaultNames = { "Johnny Deep", "Icewallowcum", "Tiny cox", "Moe Lester", "Ben Dover" };
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

    private boolean doesNameExist(String name) {
        List<ClientHandler> connectedClients = session.getConnectedPlayers();
        for (ClientHandler c : connectedClients) {
            if (c.getName().equals(name))
                return true;
        }
        return false;
    }

}
