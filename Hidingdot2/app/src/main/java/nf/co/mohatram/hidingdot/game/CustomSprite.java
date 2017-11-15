package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by madan on 5/9/17.
 */

public class CustomSprite extends Sprite {
   /* public CustomSprite() {
        super();
        init();
    }*/

    public CustomSprite(Texture texture) {
        super(texture);
        init();
    }

    /*public CustomSprite(Texture texture, int srcWidth, int srcHeight) {
        super(texture, srcWidth, srcHeight);
        init();
    }*/

    /*public CustomSprite(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(texture, srcX, srcY, srcWidth, srcHeight);
        init();
    }

    public CustomSprite(TextureRegion region) {
        super(region);
        init();
    }

    public CustomSprite(TextureRegion region, int srcX, int srcY, int srcWidth, int srcHeight) {
        super(region, srcX, srcY, srcWidth, srcHeight);
        init();
    }

    public CustomSprite(Sprite sprite) {
        super(sprite);
        init();
    }*/

    private void init() {
        setOrigin(getWidth()/2f, getHeight()/2f);
        setAlpha(0.0f);
        setScale(2f);
        if (getTexture()!=null){
            getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        }
    }
}
