package com.click.studios.furysky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new SplashView(this));

        SharedPreferences sharedPreferences=getSharedPreferences("score_data", MODE_PRIVATE);
        Global.SCORE=sharedPreferences.getLong("score", -1);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class SplashView extends SurfaceView implements SurfaceHolder.Callback {

        public SplashView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {

            SurfaceHolder holder1 = getHolder();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);

            float scaleFactor_x = getWidth() / 640.0f, scaleFactor_y = getHeight() / 960.0f;

            Canvas canvas = holder1.lockCanvas();
            int savedState = canvas.save();
            canvas.scale(scaleFactor_x, scaleFactor_y);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restoreToCount(savedState);
            holder1.unlockCanvasAndPost(canvas);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                    }
                    Intent intent = new Intent("com.click.studios.furysky.GAME_MENU");

                    startActivity(intent);
                    finish();
                }
            };
            thread.start();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }

    }
}
