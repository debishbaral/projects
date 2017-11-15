package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by madan on 5/5/17.
 */

public class FinishArea extends ObjectView {
    public static final Color finish_color_area =new Color(0f, 0f, 1f, 1f); //blue
    public static final int view_type=1;

    private Texture texture;

    public FinishArea(ObjectProperty objectProperty, Texture texture) {
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
