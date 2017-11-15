/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 *
 * @author madan
 */
public class GameThread extends Thread {

    boolean running = false;
    int FPS = 30;
    GameView gameView;
    final SurfaceHolder surfaceHolder;
    Canvas canvas;
    boolean paused = false;

    public GameThread(GameView gameView, SurfaceHolder surfaceHolder) {
        this.gameView = gameView;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        int savedState;

        canvas = surfaceHolder.lockCanvas();
        savedState = canvas.save();
        canvas.scale(GameFactory.SCALE_FACTOR_X, GameFactory.SCALE_FACTOR_Y);
        gameView.draw(canvas);
        canvas.restoreToCount(savedState);
        surfaceHolder.unlockCanvasAndPost(canvas);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
            Log.i("Debug", ex.getMessage());
        }

        while (running) {
            synchronized (this) {
                if (paused) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            //draw
            synchronized (surfaceHolder) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    savedState = canvas.save();
                    canvas.scale(GameFactory.SCALE_FACTOR_X, GameFactory.SCALE_FACTOR_Y);
                    gameView.draw(canvas);
                    canvas.restoreToCount(savedState);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    Log.i("Debug", e.getMessage());
                }
            }
            //update
            gameView.update();

            //pause
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void pause() {
        synchronized (this) {
            paused = true;
        }
    }

    public void resumeThread() {
        synchronized (this) {
            paused = false;
            this.notifyAll();
        }
    }

    public void setRunning(boolean b) {
            running = b;
    }
}
