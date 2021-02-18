package common;

public interface MessageResponseListener {
    void onSuccess(Message msg);
    void onError(String errorMsg);
}
