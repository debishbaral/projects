package com.example.madan.wifimessaging;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by madan on 1/6/17.
 */

public class WmsServer implements Runnable {

    public int serverPort = 45678;

    private boolean isRunning;
    private ServerSocket serverSocket;
    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<Thread> threadPools = new ArrayList<>();

    public WmsServer(int serverPort) throws IOException {
        this.serverPort = serverPort;
        isRunning = false;
        sockets = new ArrayList<>();
        serverSocket = new ServerSocket(this.serverPort);
        WMSServerState.serverCreated=true;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        Socket socket;
        while (isRunning) {
            try {
                socket = serverSocket.accept();
                sockets.add(socket);
                MessagePoller p = new MessagePoller(socket);
                threadPools.add(p);
                p.setRunning(true);
                p.start();
                WMSServerState.serverRunning=true;
            } catch (IOException e) {
                isRunning=false;
                WMSServerState.serverRunning=false;
            }
        }
    }

    public void stop() {
        setRunning(false);
        for (Thread messagePoller : threadPools) {
            ((MessagePoller) messagePoller).setRunning(false);
            ((MessagePoller) messagePoller).closeSocket();
        }
        threadPools.clear();
        sockets.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {

        }
    }

    private class MessagePoller extends Thread {

        private boolean running;
        private Socket socket;

        private MessagePoller(Socket socket) {
            running = false;
            this.socket = socket;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    Message message = (Message) inputStream.readObject();
                    WmsServer.this.sendMessageToAll(message);
                } catch (ClassNotFoundException e) {
                    if(Global.mainActivity!=null) Global.mainActivity.onServerError(e);
                } catch (IOException e) {
                    if(Global.mainActivity!=null) Global.mainActivity.onServerError(e);
                    closeSocket();
                    break;
                }
            }
        }

        private void closeSocket() {
            try {
                socket.close();
                sockets.remove(socket);
                running = false;
            } catch (IOException e) {

            }
        }
    }


    private synchronized void sendMessageToAll(Message message) {
        for (Socket s : sockets) {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                try {
                    sockets.remove(s);
                    s.close();
                } catch (IOException e1) {

                }
            }
        }
    }

    private static class WMSServerState{
        public static boolean serverCreated=false;
        public static boolean serverRunning=false;
    }
}