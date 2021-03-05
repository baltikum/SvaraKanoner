package common;

/**
 * This is the base of any phase that wants to be part of the game, both on server side and client side.
 *
 * @author Jesper Jansson
 * @version 05/03/21
 */
public interface Phase {

    /**
     * Messages not handled by common parts of the client/server will be forwarded to the active
     * phase for the session through this function.
     * @param msg The message from the server/client.
     */
    void message(Message msg);

}
