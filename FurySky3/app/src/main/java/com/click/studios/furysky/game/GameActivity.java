/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 *
 * @author madan
 */
public class GameActivity extends Activity{

    /**
     * Called when the activity is first created.
     * @param icicle
     */
    GameView gameView;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        gameView=new  GameView(this);
        setContentView(gameView);
        GameFactory.setGameActivity(this);
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    
    @Override
    public void onBackPressed(){
        GameFactory.setPauseTime();
        Intent intent=new  Intent(GameActivity.this, GamePauseDialog.class);
        startActivity(intent);
        gameView.gameThread.pause();
    }
}
