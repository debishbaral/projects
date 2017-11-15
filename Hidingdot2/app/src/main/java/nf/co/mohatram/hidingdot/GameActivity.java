package nf.co.mohatram.hidingdot;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

import nf.co.mohatram.hidingdot.game.HidingDotGame;

public class GameActivity extends AndroidApplication {
    public LevelModel levelModel;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DotApplication.get().getDatabaseHelper();
        Intent intent = getIntent();
        levelModel = new LevelModel();
        levelModel.decodeFromIntent(intent);
        initialize(new HidingDotGame(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MusicService.get() != null) MusicService.get().pauseMusic();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_frm_right, R.anim.anim_slide_to_left_exit);
        if (databaseHelper.isMusicOn()) {
            if (MusicService.get() != null) MusicService.get().resumeMusic();
        }
    }

}
