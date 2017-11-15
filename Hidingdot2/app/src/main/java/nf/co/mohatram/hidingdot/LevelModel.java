package nf.co.mohatram.hidingdot;

import android.content.Intent;

/**
 * Created by madan on 4/30/17.
 */

public class LevelModel {
    public boolean levelLocked;
    public int noOfStars;
    public String levelFileName;
    public int levelId;
    public int levelNo;

    public  void encodeToIntent(Intent intent){
        intent.putExtra(key_level_locked, levelLocked);
        intent.putExtra(key_no_of_stars, noOfStars);
        intent.putExtra(key_level_file, levelFileName);
        intent.putExtra(key_level_id, levelId);
    }

    public void decodeFromIntent(Intent intent){
        levelLocked=intent.getBooleanExtra(key_level_locked, true);
        levelId=intent.getIntExtra(key_level_id, 0);
        levelFileName=intent.getStringExtra(key_level_file);
        noOfStars=intent.getIntExtra(key_no_of_stars, 0);
    }

    public static final String key_level_locked="12adf4125a";
    public static final String key_no_of_stars="bade234c1a";
    public static final String key_level_file="deadbeef12";
    public static final String key_level_id="8791ade1ac";
}
