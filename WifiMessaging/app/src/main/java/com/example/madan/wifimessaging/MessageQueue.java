package com.example.madan.wifimessaging;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by madan on 1/5/17.
 */

public class MessageQueue {
    private static ArrayList<MessageComponents> messageComponents=new ArrayList<>();

    public static void addMessage(Message message, Context context){
        messageComponents.add(new MessageComponents(context, message));
    }

    public static ArrayList<MessageComponents> getMessageComponents(){
        return messageComponents;
    }
}
