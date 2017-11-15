package com.example.madan.wifimessaging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by madan on 1/6/17.
 */


public class WmsClient implements Runnable {
    public static final int NORMAL=0;
    public static  final int WITH_EXCEPTION=-1;
    private boolean running;
    private Socket socket;
    private final String serverIP;
    private final int serverPort;
    private Context context;


    public WmsClient(String serverIP, int serverPort, Context context) throws IOException {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.context = context;
        socket = new Socket(this.serverIP, this.serverPort);
        WMSClientState.clientCreated=true;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    Message message = new Message("SERVER\n000", Setting.USER_NAME + " joined the group.", 0, 0, 0, Setting.USER_ID);
                    outputStream.writeObject(message);
                    WMSClientState.clientActive = true;
                } catch (InterruptedException e) {

                } catch (IOException e) {
                    WMSClientState.clientActive = false;
                    running = false;
                }
            }
        }).start();

        while (running) {
            try {

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) inputStream.readObject();
                MessageQueue.addMessage(message, context);
                if (Global.mainActivity != null) Global.mainActivity.updateMessage(); //on ui thread

            } catch (ClassNotFoundException e) {
            } catch (IOException e) {
                stop(WITH_EXCEPTION);
                running = false;
            }
        }
    }

    public synchronized void sendMessage(Message message){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);
            outputStream.flush();
        }catch (IOException e){
            Log.e("check", "madan", e);
            Toast.makeText(context, "Oops! unable to connect to host", Toast.LENGTH_SHORT).show();
            stop(WITH_EXCEPTION);
        }
    }

    public void stop(int reason) {
        try {
            running = false;
            socket.close();
            WMSClientState.clientActive=false;
        } catch (IOException e) {
            if(Global.mainActivity!=null) Global.mainActivity.clientIsClosed(reason);
        }
    }

    public static class WMSClientState {
        public static boolean clientCreated = false;
        public static boolean clientActive = false;
    }
}


