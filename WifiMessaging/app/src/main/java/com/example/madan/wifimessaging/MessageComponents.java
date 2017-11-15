package com.example.madan.wifimessaging;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by madan on 1/5/17.
 */

public class MessageComponents {
    public Drawable drawable;
    public String name;
    public String message;
    public int id;

    public MessageComponents(Context context, Message message){
        drawable=GroupListAdaptor.getTextDrawable(context, message.userName, message.r, message.g, message.b);
        name=message.userName+"\n"+((message.senderId==-1)?123:message.senderId);
        this.message=message.message;
        this.id=message.senderId;
    }
}
