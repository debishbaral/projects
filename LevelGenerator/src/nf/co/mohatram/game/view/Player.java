package nf.co.mohatram.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import nf.co.mohatram.hidingdot.game.ObjectProperty;
import nf.co.mohatram.hidingdot.game.ObjectView;

/**
 * Created by madan on 4/22/17.
 */
public class Player extends ObjectView {
    public static final Color PLAYER_COLOR_NORMAL=Color.BLACK;
    public static final Color PLAYER_COLOR_HINT=Color.BLUE;

    private Texture textureNormal;
    private Texture textureHintActive;
    private boolean hintActive=false;

    public Player(ObjectProperty objectProperty, Texture textureNormal, Texture textureHintActive) {
        super(objectProperty);
        this.textureNormal=textureNormal;
        this.textureHintActive=textureHintActive;
    }


    @Override
    public void dispose() {
        textureNormal.dispose();
        textureHintActive.dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        Texture texture;
        if (isHintActive()) texture=textureHintActive;
        else texture=textureNormal;

        spriteBatch.draw(texture,
                objectProperty.position.x-objectProperty.dimension.x/2f,
                objectProperty.position.y-objectProperty.dimension.y/2f,
                objectProperty.dimension.x,
                objectProperty.dimension.y);;
    }

    public boolean isHintActive() {
        return hintActive;
    }

    public void setHintActive(boolean hintActive) {
        this.hintActive = hintActive;
    }
}

/*private void initialize() {
        //normal texture
        Pixmap pixmapNormal=new Pixmap((int)objectProperty.dimension.x,
                (int)objectProperty.dimension.y,
                Pixmap.Format.RGBA4444);
        pixmapNormal.setColor(Player.PLAYER_COLOR_NORMAL);
        pixmapNormal.fill();
        textureNormal=new Texture(pixmapNormal);
        pixmapNormal.dispose();

        //hint texture
        Pixmap pixmapHintActive=new Pixmap((int)objectProperty.dimension.x,
                (int)objectProperty.dimension.y,
                Pixmap.Format.RGBA4444);
        pixmapHintActive.setColor(Player.PLAYER_COLOR_HINT);
        pixmapHintActive.fill();
        textureHintActive=new Texture(pixmapHintActive);
        pixmapHintActive.dispose();

    }*/