package nf.co.mohatram.hidingdot;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by madan on 4/30/17.
 */

public class LevelSelectorModel{
    public boolean levelLocked;
    public int noOfStars;
    public String levelFileName;

    public LevelSelectorModel() {
        levelLocked = true;
        noOfStars = 0;
        levelFileName = "level1.json";
    }

    public LevelSelectorModel(boolean levelLocked, int noOfStars, String levelFileName) {
        this.levelLocked = levelLocked;
        this.noOfStars = noOfStars;
        this.levelFileName = levelFileName;
        if (levelLocked) this.noOfStars = 0;
    }
}
