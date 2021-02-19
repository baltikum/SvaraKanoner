package common;

/**
 * Used to get a response from a message upon either success or failure.
 * @author Jesper Jansson
 * @version 19/02/21
 */
public interface MessageResponseListener {
    void onSuccess(Message msg);
    void onError(String errorMsg);
}
