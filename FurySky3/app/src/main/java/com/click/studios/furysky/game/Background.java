/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 *
 * @author madan
 */
public class Background {
    private Bitmap backgroundImage;
    private float x,y;
    private float velocity;
    public Background(Bitmap res){
        backgroundImage=res;
        x=y=0;
        velocity=10;
    }
    
    public void draw(Canvas canvas){
        canvas.drawBitmap(backgroundImage, x, y, null);
        canvas.drawBitmap(backgroundImage, x+GameFactory.GAME_WIDTH, y, null);
    }
    
    public void update(){
        x-=velocity;
        if(x<=(-GameFactory.GAME_WIDTH)) x=0;
    }
    
    public void setBackground(Bitmap res){
        backgroundImage=res;
    }
    public void setVelocity(float velocity){
        this.velocity=velocity;
    }
}
