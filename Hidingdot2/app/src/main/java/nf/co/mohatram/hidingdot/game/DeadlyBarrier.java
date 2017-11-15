package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by madan on 5/5/17.
 */

public class DeadlyBarrier extends ObjectView {
    public static final Color deadly_barrier_color =new Color(1f, 0f, 0f, 1f);
    public static final int view_type=3;
    private Texture texture;

    public DeadlyBarrier(ObjectProperty objectProperty, Texture texture) {
        super(objectProperty);
        this.texture = texture;
    }

    @Override
    public int getType() {
        return view_type;
    }



    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
    }
}
