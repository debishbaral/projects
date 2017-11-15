package nf.co.mohatram.hidingdot.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nf.co.mohatram.hidingdot.game.CustomAnimation;
import nf.co.mohatram.hidingdot.game.WinAnimation;

/**
 * Created by madan on 5/10/17.
 */

public abstract class WinLoseAnimation extends CustomAnimation {
    private Sprite winLoseSprite;
    private float elapsedTime;
    public WinLoseAnimation(float duration, boolean win) {
        super(duration);
        Gdx.input.setInputProcessor(this);
        if (win){
            winLoseSprite=new Sprite(new Texture(Gdx.files.internal("tutorial/win.png"), true));
        }else {
            winLoseSprite=new Sprite(new Texture(Gdx.files.internal("tutorial/penalty.png"), true));
        }
        winLoseSprite.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        winLoseSprite.setBounds(174f, 166f, 472f, 156f);

        winLoseSprite.setOrigin(winLoseSprite.getWidth()/2f, winLoseSprite.getHeight()/2f);
        elapsedTime = 0;
    }

    @Override
    public void update(float dt) {
        elapsedTime=Math.min(elapsedTime+dt, getDuration());
        float scale= WinAnimation.CustomAccelerateInterpolator.get().ofFloat(getDuration(), elapsedTime, 2f, 1f);
        float alpha=WinAnimation.CustomAccelerateInterpolator.get().ofFloat(getDuration(), elapsedTime, 0f, 1f);

        winLoseSprite.setAlpha(alpha);
        winLoseSprite.setScale(scale);
    }

    @Override
    public void dispose() {
        winLoseSprite.getTexture().dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        winLoseSprite.draw(spriteBatch);
    }
}
