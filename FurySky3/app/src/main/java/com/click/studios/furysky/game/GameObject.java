/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Rect;

/**
 *
 * @author madan
 */
public abstract class GameObject {
    protected int pos_x, pos_y;
    protected int vel_x, vel_y;
    protected int width, height;
    protected int acc_x, acc_y;
    protected  int temp_vel_x, temp_vel_y;
    
    
    public void setPosX(int x){pos_x=x;}
    public void setPosY(int y){pos_y=y;}
    public void setVelX(int dx){vel_x=dx;}
    public void setVelY(int dy){vel_y=dy;}
    public int getVelY(){return vel_y;}
    public int getVelX(){return  vel_x;}
    public void setWidth(int w){width=w;}
    public void setHeight(int h){height=h;}
    public void setDimension(int w, int h){width=w;height=h;}
    public int getPosX(){return pos_x;}
    public int getPosY(){return  pos_y;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
     public void setAccX(int x){acc_x=x;}
    public void setAccY(int y){acc_y=y;}
    
    public Rect getRectangle(){
        return new Rect(pos_x, pos_y,pos_x+width, pos_y+height);
    }
}
