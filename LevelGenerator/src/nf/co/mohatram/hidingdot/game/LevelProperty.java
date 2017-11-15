package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.utils.Array;


public class LevelProperty {
    public Array<ObjectProperty> visibleAreaProperties;
    public Array<ObjectProperty> friendlyBarriersProperties;
    public Array<ObjectProperty> deadlyBarriersProperties;
    public Array<ObjectProperty> starsProperties;

    public ObjectProperty playerProperty;
    public ObjectProperty finishPointAreaProperty;

    public float levelHeight, levelWidth;
    public int breakRow, breakCol;//break screen into no of rows and col

    public LevelProperty(){
        visibleAreaProperties =new Array<>();
        friendlyBarriersProperties =new Array<>();
        deadlyBarriersProperties =new Array<>();
        starsProperties =new Array<>();
    }


}
