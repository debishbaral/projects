/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.Random;

/**
 *
 * @author madan
 */
public class Missile extends GameObject{
    int noOfMissileFrames=4;
    Random random;
    int frame=0;
    int movement;
    
    public Missile(){
        
        width=GameFactory.MISSILE_IMAGES[0].getWidth();
        height=GameFactory.MISSILE_IMAGES[0].getHeight();
        
        random=new Random();
        pos_x=(int) (GameFactory.GAME_WIDTH + width);
        pos_y=random.nextInt((int) GameFactory.GAME_HEIGHT);
        vel_y=10;
        vel_x=15;
        temp_vel_x=vel_x;
        acc_x=0;
        movement=random.nextInt(3);
    }
    
    public void draw(Canvas canvas){
        canvas.drawBitmap(GameFactory.MISSILE_IMAGES[frame], pos_x, pos_y, null);
        canvas.drawBitmap(GameFactory.MISSILE_IMAGES[frame], pos_x, (pos_y+GameFactory.GAME_HEIGHT/2)%(GameFactory.GAME_HEIGHT), null);
    }
    
    public void update(){
        frame=(frame+1)%noOfMissileFrames;
        pos_x-=temp_vel_x;
        
        temp_vel_x+=acc_x;
        if(movement==0){
            return;
        }else if(movement==1){
            pos_y+=vel_y;
            pos_y=(int) (pos_y%GameFactory.GAME_HEIGHT);
        }else{
            pos_y-=vel_y;
            if(pos_y<0) pos_y+=GameFactory.GAME_HEIGHT;
        }
    }
}
