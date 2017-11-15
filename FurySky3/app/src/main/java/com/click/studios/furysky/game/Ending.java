/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.click.studios.furysky.Global;
import com.click.studios.furysky.R;

/**
 *
 * @author madan
 */
public class Ending extends Activity {

    /**
     * Called when the activity is first created.
     * @param icicle
     */
    Bundle bundle;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here   
        
        bundle=icicle;
        
        //full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(new EndingView(this));
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    
    public class EndingView extends SurfaceView implements SurfaceHolder.Callback{

        public EndingView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            
            if(event.getAction()!=MotionEvent.ACTION_UP){
                return true;
            }
            float x=event.getX();
            float ratioX=x/GameFactory.VIEW_WIDTH;
            float y=event.getY();
            float ratioY=y/GameFactory.VIEW_HEIGHT;
            if(ratioY<0.65f){
                return true;
            }
            Log.i("Debug", Float.toString(ratioX));
            //
            if(ratioX<0.5f){
                finish();
            }else{
                Intent intent=new Intent(Ending.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
            return false;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            SurfaceHolder surfaceHolder;
            Canvas canvas;
            int savedState;
            Paint paint=new Paint();
            paint.setColor(Color.LTGRAY);
            paint.setTextSize(100);
            Bitmap image=BitmapFactory.decodeResource(getResources(), R.drawable.ending);
            Bitmap backGround=BitmapFactory.decodeResource(getResources(), R.drawable.background);
            
            surfaceHolder=getHolder();
            canvas=surfaceHolder.lockCanvas();
            savedState=canvas.save();
            canvas.scale(GameFactory.SCALE_FACTOR_X, GameFactory.SCALE_FACTOR_Y);
            canvas.drawBitmap(backGround, 0, 0, null);
            canvas.drawBitmap(image, 0, 0, null);
            canvas.drawText(Integer.toString(GameFactory.SCORE), 300, 250, paint);

            paint.setTextSize(35);
            if(Global.SCORE<GameFactory.SCORE){
                canvas.drawText("You saved the queen "+GameFactory.SCORE, 300, 600, paint);
                SharedPreferences sharedPreferences=getSharedPreferences("score_data", MODE_PRIVATE);
                sharedPreferences.edit().putLong("score", GameFactory.SCORE).commit();
            }else {
                canvas.drawText("High score is "+Global.SCORE, 300, 600, paint);
            }
            canvas.restoreToCount(savedState);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
        
    }
}
