package nf.co.mohatram.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import nf.co.mohatram.hidingdot.game.ObjectProperty;
import nf.co.mohatram.hidingdot.game.ObjectView;

/**
 * Created by madan on 4/22/17.
 */
public class DeadlyBarrier extends ObjectView {
    public static final Color COLOR_FOR_THIS_OBJECT =Color.RED;

    private Texture texture;
    public DeadlyBarrier(ObjectProperty objectProperty, Texture texture) {
        super(objectProperty);
        this.texture=texture;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture,
                objectProperty.position.x-objectProperty.dimension.x/2f,
                objectProperty.position.y-objectProperty.dimension.y/2f,
                objectProperty.dimension.x,
                objectProperty.dimension.y);
    }
}
