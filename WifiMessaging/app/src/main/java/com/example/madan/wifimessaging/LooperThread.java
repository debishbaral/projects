package com.example.madan.wifimessaging;

import android.os.Looper;

import android.os.Handler;

/**
 * Created by madan on 1/6/17.
 */

public class LooperThread extends Thread {
    private Handler handler;
    @Override
    public void run() {
        Looper.prepare();
        handler=new Handler() ;
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }
}
