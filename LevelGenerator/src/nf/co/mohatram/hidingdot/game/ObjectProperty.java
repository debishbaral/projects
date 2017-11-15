package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ObjectProperty{
    public Vector2 position;
    public Vector2 dimension;


    public ObjectProperty(){
        position=new Vector2();
        dimension=new Vector2();
    }


    public ObjectProperty(float x, float y, float width, float height){
        this();
        position.set(x, y);
        dimension.set(width, height);
    }

    public Rectangle getBoundingRectangle(){
        Rectangle rectangle=new Rectangle().set(
                position.x, //x position
                position.y, //y position
                dimension.x, //width
                dimension.y); //height
        return rectangle;
    }
}
