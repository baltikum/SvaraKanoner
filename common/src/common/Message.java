package common;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all messages between the server and the client and vice versa.
 */
public class Message implements Serializable {

    public enum Type {
        // Both way messages
        RESPONSE,

        // Server to client messages
        PLAYER_CONNECTED,      // JoinPhase -> JoinPhase
        PLAYER_DISCONNECTED,   // JoinPhase -> JoinPhase
        PLAYER_READY_STATUS_CHANGED,   // JoinPhase -> JoinPhase
        GOTO_PICK_WORD_PHASE,          // JoinPhase -> JoinPhase
        SEND_WORD_CHOICES,

        // Client to server messages
        CREATE_GAME,           // MainMenu -> Server
        JOIN_GAME,             // MainMenu -> Server
        DISCONNECT,            // JoinPhase -> JoinPhase
        TOGGLE_READY_STATUS,   // JoinPhase -> JoinPhase
        PICK_WORD,             // PickWordPhase -> PickWordPhase
    }

    public transient Player player;

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
        if (error != null) {
            return "Message{" +
                    ", type=" + type +
                    ", errorMsg=" + error +
                    '}';
        } else {
            return "Message{" +
                    ", type=" + type +
                    ", data=" + data +
                    '}';
        }
    }
}
