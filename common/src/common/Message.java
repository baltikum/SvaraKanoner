package common;



/**
 * Represents all messages between the server and the client and vice versa.
 */
public class Message {

    enum Type {
        CREATE_SERVER, // MainMenu -> Server
        JOIN_SERVER,   // MainMenu -> Server
        START_GAME,    // JoinPhase (client) -> JoinPhase (server)
        LEAVE_GAME,    // JoinPhase (client) -> JoinPhase (server)
    }

    public int id;
    public Type type;
    public Map<String, Serializable> data;

}
