package nf.co.mohatram.grescrabble;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by madan on 7/2/17.
 */

public class NetworkThread extends Thread {
    public static NetworkThread networkThread;

    public Handler handler;
    NetworkThread(){}

    @Override
    public void run() {
        Looper.prepare();
        handler=new Handler();
        Looper.loop();
    }




}
