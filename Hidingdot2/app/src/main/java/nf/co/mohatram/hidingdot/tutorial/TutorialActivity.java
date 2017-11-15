package nf.co.mohatram.hidingdot.tutorial;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

import nf.co.mohatram.hidingdot.DatabaseHelper;
import nf.co.mohatram.hidingdot.DotApplication;
import nf.co.mohatram.hidingdot.MusicService;

public class TutorialActivity extends AndroidApplication {
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(new GameTutorial(this));
        databaseHelper= DotApplication.get().getDatabaseHelper();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (MusicService.get() != null) MusicService.get().pauseMusic();
//        Log.e("Unwanted execution", "Tutotial activiity onPause");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("Unwanted execution", "Tutotial activiity onResume");
        if (databaseHelper.isMusicOn()) {
            if (MusicService.get() != null) MusicService.get().resumeMusic();
        }
    }
}
