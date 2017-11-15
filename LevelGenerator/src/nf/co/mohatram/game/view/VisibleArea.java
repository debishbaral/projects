package nf.co.mohatram.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import nf.co.mohatram.hidingdot.game.ObjectProperty;
import nf.co.mohatram.hidingdot.game.ObjectView;

/**
 * Created by madan on 4/22/17.
 */
public class VisibleArea extends ObjectView {

    private Texture texture;
    public VisibleArea(ObjectProperty objectProperty, Texture texture) {
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

/*    private void initialize(){
        Pixmap pixmap=new Pixmap((int)objectProperty.dimension.x,
                (int)objectProperty.dimension.y,
                Pixmap.Format.RGBA4444);
        pixmap.setColor(VisibleArea.COLOR_FOR_THIS_OBJECT);
        this.texture=new Texture(pixmap);
        pixmap.dispose();
    }*/
}
