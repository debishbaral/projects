/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Bitmap;

/**
 *
 * @author madan
 * 
 * Set this class only start of game.
 */
public class GameFactory {
    
    public static GameActivity GAME_ACTIVITY;
    public static float VIEW_HEIGHT;
    public static float VIEW_WIDTH;
    public static float SCALE_FACTOR_X, SCALE_FACTOR_Y;
    public static float GAME_WIDTH=960.0f, GAME_HEIGHT=640.0f;
    public static long PAUSE_TIME=0;
    public static int PAUSE_TIME_SCORE=0;
    public static int SCORE=0;
    public static Bitmap[] MISSILE_IMAGES=new Bitmap[4];
    
    public static void setViewHeight(float viewHeight){
        VIEW_HEIGHT=viewHeight;
        SCALE_FACTOR_Y=VIEW_HEIGHT/GAME_HEIGHT;
    }
    public static void setViewWidth(float viewWidth){
        VIEW_WIDTH=viewWidth;
        SCALE_FACTOR_X=VIEW_WIDTH/GAME_WIDTH;
    }
    public static void setViewDimension(float width, float height){
        VIEW_HEIGHT=height;VIEW_WIDTH=width;
        SCALE_FACTOR_Y=VIEW_HEIGHT/GAME_HEIGHT;
        SCALE_FACTOR_X=VIEW_WIDTH/GAME_WIDTH;
        //Log.i("Debug", Float.toString(VIEW_WIDTH)+" "+Float.toString(VIEW_HEIGHT));
    }

    public static void setGameActivity(GameActivity gameActivity){
        GAME_ACTIVITY=gameActivity;
    }
    
    public static void setPauseTime(){
        PAUSE_TIME=System.currentTimeMillis();
    }
    
    public static void setPauseTimeScore(){
        PAUSE_TIME_SCORE+=(int)(System.currentTimeMillis()-PAUSE_TIME)/100;
    }
    
    public static void setScore(int score){
        SCORE=score;
    }
    
    public static void setMissileBitmap(Bitmap resBitmap){
        for(int i=0; i<4; i++){
            int height=resBitmap.getHeight();
            int width=resBitmap.getWidth()/4;
            MISSILE_IMAGES[i]=Bitmap.createBitmap(resBitmap, i*width, 0, width, height);
        }
    }
}
