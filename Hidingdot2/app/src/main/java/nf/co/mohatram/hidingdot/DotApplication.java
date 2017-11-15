package nf.co.mohatram.hidingdot;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

/**
 * Created by madan on 5/14/17.
 */

public class DotApplication extends MultiDexApplication {
    private static DotApplication application;
    public static final int current_database_version = 2;

    public static DotApplication get() {
        return application;
    }

    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(DotApplication.this, current_database_version);

        application = this;

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        application = null;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    /*public MusicService getMusicService() {
        return musicService;
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }*/
}
