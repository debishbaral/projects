/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.click.studios.furysky.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.click.studios.furysky.GameMenu;
import com.click.studios.furysky.R;

/**
 *
 * @author madan
 */
public class GamePauseDialog extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param icicle
     */
    Context context;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.dialog);
        Button exit = (Button) findViewById(R.id.exit);
        Button resume = (Button) findViewById(R.id.resume);
        exit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                GameFactory.GAME_ACTIVITY.gameView.player.setDead(true);
                GameFactory.GAME_ACTIVITY.gameView.gameThread.resumeThread();
                GamePauseDialog.this.finish();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                GameFactory.setPauseTimeScore();
                GameFactory.GAME_ACTIVITY.gameView.gameThread.resumeThread();
                GamePauseDialog.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        GameFactory.setPauseTimeScore();
        GameFactory.GAME_ACTIVITY.gameView.gameThread.resumeThread();
        finish();
    }
}
