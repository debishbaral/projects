package nf.co.mohatram.hidingdot;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by madan on 5/15/17.
 */

public class AdRequestThread extends Thread {
    public Handler handler;

    public void run(){
        Looper.prepare();
        handler=new Handler();
        Looper.loop();
    }
}
