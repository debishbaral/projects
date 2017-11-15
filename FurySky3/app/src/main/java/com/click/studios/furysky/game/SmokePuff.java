/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 *
 * @author madan
 */
public class SmokePuff extends GameObject{
    
    private int radius=10;
    public SmokePuff(int x, int y){
        this.pos_x=x;this.pos_y=y;
    }
    
    public void draw(Canvas canvas){
        Paint paint=new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pos_x-radius, pos_y-radius, radius, paint);
        canvas.drawCircle(pos_x-radius+5, pos_y-radius+5, radius, paint);
        canvas.drawCircle(pos_x-radius+3, pos_y-radius-5, radius, paint);
    }
    public void update(){
        pos_x-=15;
    }
}
