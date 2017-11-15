/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 *
 * @author madan
 */
public class Helicopter extends GameObject {

    int noOfFramesImages = 3;
    Bitmap sprite;
    Bitmap heliImages[] = new Bitmap[noOfFramesImages];
    int frameNo = 0;
    public boolean up = false;
    long startTime;
    boolean playedOnce = false;
    boolean dead = false;
    GameThread gameThread;
    int explosionFrames = 0;
    int score;
    Context context;
    GameActivity gameActivity;
    Paint paint;

    public Helicopter(Context context, Bitmap sprite, GameThread gameThread) {
        this.context=context;
        this.gameActivity=(GameActivity) context;
        this.gameThread = gameThread;
        this.sprite = sprite;
        width = sprite.getWidth() / noOfFramesImages;
        height = sprite.getHeight() / 2;
        startTime = System.currentTimeMillis();

        for (int i = 0; i < noOfFramesImages; i++) {
            heliImages[i] = Bitmap.createBitmap(sprite, i * width, 0, width, height);
        }

        pos_x = 100;
        pos_y = 0;
        vel_x = 0;
        vel_y = 5;
        acc_x = 0;
        acc_y = 1;
        paint=new Paint();
        paint.setTextSize(70);
        paint.setColor(Color.GRAY);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(heliImages[frameNo], pos_x, pos_y, null);
        canvas.drawText("Score::"+Integer.toString(score-GameFactory.PAUSE_TIME_SCORE), 10, 620, paint);
    }

    public void update() {
        
        //for animation of heli.
        frameNo = (frameNo + 1) % noOfFramesImages;
        
        if (!dead) {
            if (up) {
                pos_y -= temp_vel_y;
            } else {
                pos_y += temp_vel_y;
            }
        } else{
            //Log.i("Debug", "Working");
            if(explosionFrames<1) for (int i = 0; i < noOfFramesImages; i++) {
                heliImages[i] = Bitmap.createBitmap(sprite, i * width, height, width, height);
            }
            explosionFrames++;
            if (explosionFrames > 10) {
               gameThread.setRunning(false);
               GameFactory.setScore(score-GameFactory.PAUSE_TIME_SCORE);
               Intent i=new Intent(context, Ending.class);
               context.startActivity(i);
               gameActivity.finish();
            }
        }
        
        //accleration.
        temp_vel_y += acc_y;
        
        
        //for collision detection.
        if (pos_y < 0 | pos_y > GameFactory.GAME_HEIGHT - height) {
            dead = true;
        }
        
        
        //update score.
        long stopTime = System.currentTimeMillis();
        score = (int) (stopTime - startTime) / 100;

    }

    public void setUp(boolean b) {
        if (b != up) {
            temp_vel_y = vel_y;
        }
        up = b;
    }
    
    public void setDead(boolean  b){
        dead=b;
    }
}
