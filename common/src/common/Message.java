package common;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all messages between the server and the client and vice versa.
 */
public class Message implements Serializable {

    /**
     * Describes if the receiver should handle this message or forward/drop it.
     */
    public enum Type {
        // Both way messages
        RESPONSE,
        CHAT_MESSAGE,

        // Server to client messages
        CONNECTION_SUCCESS,
        GOTO,

        PLAYER_CONNECTED,            // JoinPhase -> JoinPhase
        PLAYER_DISCONNECTED,         // JoinPhase -> JoinPhase
        PLAYER_READY_STATUS_CHANGED, // JoinPhase -> JoinPhase
        REVEAL_NEXT,                 // RevealPhase -> RevealPhase
        SOMEONE_OBJECTED,            // RevealPhase -> RevealPhase

        // Client to server messages
        CREATE_GAME,           // MainMenu -> Server
        JOIN_GAME,             // MainMenu -> Server
        DISCONNECT,            // JoinPhase -> JoinPhase
        TOGGLE_READY_STATUS,   // JoinPhase -> JoinPhase
        REVEAL_NEXT_REQUEST,   // RevealPhase -> RevealPhase
        OBJECT,                // RevealPhase -> RevealPhase

        PICK_WORD,             // PickWordPhase -> PickWordPhase

        SUBMIT_GUESS,          // GuessPhase -> GuessPhase

        SUBMIT_PICTURE,        // DrawPhase -> DrawPhase
        TIMES_UP,              // DrawPhase -> DrawPhase


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
