package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.utils.Array;

import java.io.Serializable;


public class LevelProperty implements Serializable{
    public Array<ObjectProperty> visibleAreaProperties;
    public Array<ObjectProperty> friendlyBarriersProperties;
    public Array<ObjectProperty> deadlyBarriersProperties;
    public Array<ObjectProperty> starsProperties;

    public ObjectProperty playerProperty;
    public ObjectProperty finishPointAreaProperty;

    public float levelHeight, levelWidth;
    public int breakRow, breakCol;

    public LevelProperty(){
        visibleAreaProperties =new Array<>();
        friendlyBarriersProperties =new Array<>();
        deadlyBarriersProperties =new Array<>();
        starsProperties =new Array<>();
    }
}
