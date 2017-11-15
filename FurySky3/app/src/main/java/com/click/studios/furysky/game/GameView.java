/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.click.studios.furysky.R;
import java.util.ArrayList;

/**
 *
 * @author madan
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    GameThread gameThread = null;
    Background background;
    Helicopter player;
    Context context;
    ArrayList<SmokePuff> smokePuffs;
    long smokeStartTime;

    ArrayList<Missile> missiles;
    long missileStartTime;
    long missileUpdateTime = 3000;

    public GameView(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        GameFactory.setViewDimension((float) getWidth(), (float) getHeight());
        GameFactory.SCORE=0;

        ////////////////////////////////////////////////////////////////////
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));

        gameThread = new GameThread(this, getHolder());

        player = new Helicopter(context, BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), gameThread);

        smokePuffs = new ArrayList<SmokePuff>();
        smokeStartTime = System.currentTimeMillis();

        missiles = new ArrayList<Missile>();
        missileStartTime = System.currentTimeMillis();
        GameFactory.setMissileBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.missile));

        gameThread.setRunning(true);
        gameThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        int counter = 0;
        boolean retry = true;
        while (retry & counter < 1000) {
            counter++;
            try {
                gameThread.resumeThread();
                gameThread.setRunning(false);
                gameThread.join();
                retry = false;
            } catch (InterruptedException ex) {

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            player.setUp(true);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setUp(false);
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        background.draw(canvas);
        player.draw(canvas);

        //for smoke
        for (SmokePuff smokePuff : smokePuffs) {
            smokePuff.draw(canvas);
        }

        for (Missile m : missiles) {
            m.draw(canvas);
        }
    }

    public void update() {
        background.update();

        //check collision detection.
        for (Missile m : missiles) {
            if (((player.getPosX() > m.getPosX() & player.getPosX() < m.getPosX() + m.getWidth())
                    | (player.getPosX() + player.getWidth() > m.getPosX() & player.getPosX() + player.getWidth() < m.getPosX() + m.getWidth()))
                    & ((player.getPosY() > m.getPosY() & player.getPosY() < m.getPosY() + m.getHeight())
                    | (player.getPosY() + player.getHeight() > m.getPosY() & player.getPosY() + player.getHeight() < m.getPosY() + m.getHeight())) 
                    
                    |
                    ((player.getPosX() > m.getPosX() & player.getPosX() < m.getPosX() + m.getWidth())
                    | (player.getPosX() + player.getWidth() > m.getPosX() & player.getPosX() + player.getWidth() < m.getPosX() + m.getWidth()))
                    & ((player.getPosY() >( m.getPosY()+GameFactory.GAME_HEIGHT/2 )%GameFactory.GAME_HEIGHT & player.getPosY() < ( m.getPosY()+GameFactory.GAME_HEIGHT/2 )%GameFactory.GAME_HEIGHT  + m.getHeight())
                    | (player.getPosY() + player.getHeight() > ( m.getPosY()+GameFactory.GAME_HEIGHT/2 )%GameFactory.GAME_HEIGHT  & player.getPosY() + player.getHeight() < ( m.getPosY()+GameFactory.GAME_HEIGHT/2 )%GameFactory.GAME_HEIGHT  + m.getHeight())) 
                    
                    
                    ) {

                player.setDead(true);
                break;
            }
        }

        //update player.
        player.update();

        //for smoke
        long elapsedTime = System.currentTimeMillis() - smokeStartTime;
        if (elapsedTime > 300) {
            smokePuffs.add(new SmokePuff(player.getPosX(), player.getPosY() + player.getHeight()));
            smokeStartTime = System.currentTimeMillis();
        }
        for (int i = 0; i < smokePuffs.size(); i++) {
            smokePuffs.get(i).update();
            if (smokePuffs.get(i).getPosX() < 0) {
                smokePuffs.remove(i);
            }
        }

        //for missiles
        long missileElapsedTime = System.currentTimeMillis() - missileStartTime;
        if (missileElapsedTime > missileUpdateTime) {
            missiles.add(new Missile());
            missileStartTime = System.currentTimeMillis();
            if(missileUpdateTime>500){
                missileUpdateTime-=100;
            }
        }

        for (int i = 0; i < missiles.size(); i++) {
            missiles.get(i).update();
            if (missiles.get(i).getPosX() < -145) {
                missiles.remove(i);
            }
        }

    }

}
