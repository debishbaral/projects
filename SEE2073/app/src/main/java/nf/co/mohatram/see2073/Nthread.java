package nf.co.mohatram.see2073;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * Created by madan on 6/16/17.
 */

public class Nthread extends Thread {
    public Handler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler=new Handler();
        Looper.loop();
    }

    public interface  OnCreateHandler {
        void onCreateHandler();
    }
}
