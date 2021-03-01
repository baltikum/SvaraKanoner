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
        CHAT_MESSAGE,

        // Server to client messages
        PLAYER_CONNECTED,      // JoinPhase -> JoinPhase
        PLAYER_DISCONNECTED,   // JoinPhase -> JoinPhase
        PLAYER_READY_STATUS_CHANGED, // JoinPhase -> JoinPhase
        REVEAL_NEXT,                 // RevealPhase -> RevealPhase
        GOTO_PICK_WORD_PHASE,          // JoinPhase -> JoinPhase
        SEND_WORD_CHOICES,

        IMAGE_DATA,          // GuessPhase -> GuessPhase
        GOTO_DRAW_PHASE,     // GuessPhase -> GuessPhase
        GOTO_REVEAL_PHASE,   // GuessPhase -> GuessPhase
        GOTO_WAIT_PHASE,     // GuessPhase -> GuessPhase


        WORD_DATA,     // DrawPhase -> DrawPhase
        GOTO_GUESS_PHASE,   // DrawPhase -> DrawPhase


        // Client to server messages
        CREATE_GAME,           // MainMenu -> Server
        JOIN_GAME,             // MainMenu -> Server
        DISCONNECT,            // JoinPhase -> JoinPhase
        TOGGLE_READY_STATUS,   // JoinPhase -> JoinPhase
        REVEAL_NEXT_REQUEST,   // RevealPhase -> RevealPhase

        PICK_WORD,             // PickWordPhase -> PickWordPhase

        IMAGE_DATA_RECEIVED,    // GuessPhase -> GuessPhase
        SUBMIT_GUESS,            // GuessPhase -> GuessPhase


        WORD_DATA_RECEIVED,    // DrawPhase -> DrawPhase
        SUBMIT_PICTURE,            // DrawPhase -> DrawPhase


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
