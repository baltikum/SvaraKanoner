package common;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all messages between the server and the client and vice versa.
 */
public class Message implements Serializable {

    public enum Type {
        RESPONSE,
        CREATE_GAME,           // MainMenu -> Server
        JOIN_GAME,             // MainMenu -> Server
        START_GAME,            // JoinPhase (client) -> JoinPhase (server)
        LEAVE_GAME,            // JoinPhase (client) -> JoinPhase (server)
    }

    public transient Player player;

    public int playerId;
    public Type type;
    public Map<String, Serializable> data = new HashMap<>();
    public String error = null;

    public Message(Type type) {
        this.type = type;

    }

    public void addParameter(String name, Serializable value) {
        data.put(name, value);
    }

    @Override
    public String toString() {
        return "Message{" +
                "playerId=" + playerId +
                ", type=" + type +
                ", data=" + data +
                '}';
    }
}
