package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nf.co.mohatram.hidingdot.tutorial.TutorialAnimation;

/**
 * Created by madan on 5/5/17.
 */

public class Player extends ObjectView {
    public static final Color player_color_glow =new Color(1f, 1f, 1f, 1f); //white
    public static final Color player_color_normal=new Color(0f, 0f, 0f, 1f); //black
    public static final int view_type=0;
    private Texture textureNormal;
    private Texture textureGlow;


    private boolean glowActive;

    public Player(ObjectProperty objectProperty, Texture textureNormal, Texture textureGlow) {
        super(objectProperty);
        this.textureNormal = textureNormal;
        this.textureGlow = textureGlow;
        glowActive = true;
        dt = 0;
    }

    @Override
    public int getType() {
        return view_type;
    }



    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (!glowActive){
            spriteBatch.draw(textureNormal, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
        }
        else {
            dt += Gdx.graphics.getDeltaTime();
            float v = (float) TutorialAnimation.CustomSineInterpolator.get().ofDouble(0.5f, dt, 1f);
            if ((int) (v + 0.4f) == 1) {
                spriteBatch.draw(textureNormal, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
            } else {
                spriteBatch.draw(textureGlow, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
            }
        }

    }

    public boolean isGlowActive() {
        return glowActive;
    }

    public void setGlowActive(boolean glowActive) {
        this.glowActive = glowActive;
    }

    public void toggleGlow(){
        this.glowActive=!glowActive;
    }

    private float dt;
}
