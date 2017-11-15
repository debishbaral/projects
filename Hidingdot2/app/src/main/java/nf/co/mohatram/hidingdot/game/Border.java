package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by madan on 6/8/17.
 */

public class Border extends ObjectView{
    public static final int view_type=-99;
    private Texture texture;

    private boolean active;

    public Border(ObjectProperty objectProperty, Texture texture) {
        super(objectProperty);
        this.texture = texture;
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public int getType() {
        return view_type;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (active) spriteBatch.draw(texture, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
    }


    public void setActive(boolean active) {
        this.active = active;
    }
}
