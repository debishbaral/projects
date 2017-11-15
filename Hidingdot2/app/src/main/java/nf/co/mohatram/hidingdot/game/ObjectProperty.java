package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class ObjectProperty implements Serializable{
    public Vector2 position;
    public Vector2 dimension;

    private Rectangle rectangle;

    public ObjectProperty(){
        position=new Vector2();
        dimension=new Vector2();
        rectangle=new Rectangle();
    }


    /*public ObjectProperty(float x, float y, float width, float height){
        this();
        position.set(x, y);
        dimension.set(width, height);
    }*/

    public Rectangle getBoundingRectangle(){
        return rectangle.set(
                position.x, //x position
                position.y, //y position
                dimension.x, //width
                dimension.y); //height
    }
}
