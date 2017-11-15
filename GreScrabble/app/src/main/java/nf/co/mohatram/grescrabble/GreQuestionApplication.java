package nf.co.mohatram.grescrabble;

import android.app.Application;

/**
 * Created by madan on 7/3/17.
 */

public class GreQuestionApplication extends Application {
    private static GreQuestionApplication greQuestionApplication;
    public static final GreQuestionApplication get(){
        return greQuestionApplication;
    }
    private RemoteServer remoteServer;
    private DatabaseHelper databaseHelper;
    public RemoteServer getRemoteServer() {
        return remoteServer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteServer=new RemoteServer();
        greQuestionApplication=this;
        NetworkThread.networkThread=new NetworkThread();
        NetworkThread.networkThread.start();
        databaseHelper=new DatabaseHelper(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        remoteServer.close();
        greQuestionApplication=null;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
