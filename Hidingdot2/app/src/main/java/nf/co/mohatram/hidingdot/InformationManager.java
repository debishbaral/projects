package nf.co.mohatram.hidingdot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madan on 6/3/17.
 */

public class InformationManager {
    private DatabaseHelper databaseHelper;
    private List<LevelModel> levelModels;
    private boolean soundOn;
    private boolean musicOn;
    private int noOfHints;

    public InformationManager(DatabaseHelper databaseHelper){
        levelModels=new ArrayList<>();
        this.databaseHelper=databaseHelper;
        databaseHelper.getLevelSelectorModels(levelModels);
    }

    public void setMusicOn(boolean b) {
        databaseHelper.setMusicOn(b);
        this.musicOn=b;
    }

    public int getNoOfHints() {
        return noOfHints;
    }

    public void setNoOfHints(int noOfHints) {
        databaseHelper.setNoOfHints(noOfHints);
        this.noOfHints=noOfHints;
    }

    public void addNoOfHints(int n) {
        databaseHelper.addNoOfHints(n);
        noOfHints+=n;
    }

    public void getLevelSelectorModels(List<LevelModel> levelModels) {
        databaseHelper.getLevelSelectorModels(levelModels);
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        databaseHelper.setSoundOn(soundOn);
        this.soundOn=soundOn;
    }

    public boolean isFirstTimePlayed() {
        return databaseHelper.isFirstTimePlayed();
    }

    public void setNoFirstTime() {
        databaseHelper.setNoFirstTime();
    }

    public long getLastSharedTime() {
        return databaseHelper.getLastSharedTime();
    }

    public void setLastSharedTime(long time) {
        databaseHelper.setLastSharedTime(time);
    }


}
