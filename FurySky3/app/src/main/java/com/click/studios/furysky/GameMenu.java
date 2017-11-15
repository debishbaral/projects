/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import com.click.studios.furysky.game.GameActivity;

/**
 *
 * @author madan
 */
public class GameMenu extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedState
     */
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        
        //for full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new GameMenuView(this));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class GameMenuView extends SurfaceView implements SurfaceHolder.Callback {
        
        float y;
        float ratioY=0;
        float height;
        Intent intent;
        
        
        public GameMenuView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.i("Debug", "Surface created");
            SurfaceHolder holder1 = getHolder();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_menu);

            float scaleFactor_x = getWidth() / 640.0f, scaleFactor_y = getHeight() / 960.0f;

            Canvas canvas = holder1.lockCanvas();
            int savedState = canvas.save();
            canvas.scale(scaleFactor_x, scaleFactor_y);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restoreToCount(savedState);
            holder1.unlockCanvasAndPost(canvas);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void draw(Canvas canvas) {

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP) {
                y = event.getY();
                height = getHeight();
                ratioY = y / height;
                return true;
            }

            if (ratioY >= 0.47 && ratioY <= 0.60) {
                //Log.i("Debug", "Play");
                intent = new Intent(GameMenu.this, GameActivity.class);
                startActivity(intent);
            } else if (ratioY > 0.60 && ratioY <= 0.73) {
                //Log.i("Debug", "Setting");
                intent = new Intent(GameMenu.this, Setting.class);
                startActivity(intent);
            } else if (ratioY > 0.73 && ratioY <= 0.86) {
                //Log.i("Debug", "Help");
                intent = new Intent(GameMenu.this, Help.class);
                startActivity(intent);
            } else if (ratioY > 0.86 && ratioY <= 1.0f) {
                System.exit(0);
            }

            return false;
        }
    }

}
