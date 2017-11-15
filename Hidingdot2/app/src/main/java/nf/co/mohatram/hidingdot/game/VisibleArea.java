package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by madan on 5/5/17.
 */

public class VisibleArea extends ObjectView {
    public static final Color visible_area_color=new Color(1f, 1f, 1f, 1f);
    public static final int view_type=-5;
    public Texture visibleAreaTexture;

    public VisibleArea(ObjectProperty objectProperty, Texture texture) {
        super(objectProperty);
        this.visibleAreaTexture = texture;
    }

    @Override
    public int getType() {
        return view_type;
    }


    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(visibleAreaTexture, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
    }
}
