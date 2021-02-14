package common;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents all messages between the server and the client and vice versa.
 */
public class Message implements Serializable{

    public enum Type {
        CREATE_GAME, // MainMenu -> Server
        CREATE_GAME_OK, // Server -> Client
        JOIN_GAME,   // MainMenu -> Server
        START_GAME,    // JoinPhase (client) -> JoinPhase (server)
        LEAVE_GAME,    // JoinPhase (client) -> JoinPhase (server)
    }

    public int id;
    public Type type;
    public Map<String, Serializable> data = new HashMap<>();


    public Message(Type type) {
        this.type = type;

    }


    public void addParameter(String name, Serializable value) {
        data.put(name, value);
    }

}
