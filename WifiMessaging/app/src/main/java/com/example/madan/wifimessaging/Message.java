package com.example.madan.wifimessaging;

import java.io.Serializable;

/**
 * Created by madan on 1/5/17.
 */

public class Message implements Serializable {
    int senderId;
    String userName;
    String message;
    int r, g, b;
    private static final long serialVersionUID = 5916015323142494006L;

    public Message(String userName, String message, int r, int g, int b, int senderId) {
        this.userName = userName;
        this.message = message;
        this.r = r;
        this.g = g;
        this.b = b;
        this.senderId=senderId;
    }
}
