package ru.c0ner.tprofit;

/**
 * Created by kirillf on 10/25/16.
 */
public interface AuthorizationListener {
    void onAuthStarted();

    void onComplete(String token);

    void onError(String error);
}
