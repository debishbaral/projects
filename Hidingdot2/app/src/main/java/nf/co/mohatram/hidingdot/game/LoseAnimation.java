package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nf.co.mohatram.hidingdot.MusicService;

/**
 * Created by madan on 5/9/17.
 */
public abstract class LoseAnimation extends CustomAnimation {
    private Sprite oopsText;
    private Sprite failedSeal;
    public Sprite backArrow;
    public Sprite reload;

    private float elapsedTime=0;
    public LoseAnimation(float duration) {
        super(duration);
        oopsText=new Sprite(new Texture(Gdx.files.internal("graphics/oopstext.png"), true));
        oopsText.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        failedSeal=new CustomSprite(new Texture(Gdx.files.internal("graphics/failedseal.png"), true));
        backArrow=new CustomSprite(new Texture(Gdx.files.internal("graphics/back_arrow.png"), true));
        reload=new CustomSprite(new Texture(Gdx.files.internal("graphics/reloadlevel.png"), true));

        oopsText.setBounds(224.487f, 344.910f,351.025f, 34.198f );
        failedSeal.setBounds(331.319f, 158.430f, 137.361f, 137.361f);
        backArrow.setBounds(264.885f, 52.912f, 72.000f, 61.320f);
        reload.setBounds(463.115f, 45.428f, 72.000f, 72.000f);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        elapsedTime=Math.min(elapsedTime+dt, getDuration());
        if (currentStep==STEP_SHOW_ANIMATION){
            showAnimation();
        }
        if (elapsedTime==getDuration()){
            elapsedTime=0;
            currentStep++;
        }
    }

    private void showAnimation() {
        if (!soundPlayed){
            soundPlayed=true;
            if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        }
        float alpha=WinAnimation.CustomAccelerateInterpolator.get().ofFloat(getDuration(), elapsedTime, 0f, 1f);
        float scale=WinAnimation.CustomAccelerateInterpolator.get().ofFloat(getDuration(), elapsedTime, 2f, 1f);
        failedSeal.setScale(scale);
        failedSeal.setAlpha(alpha);

        backArrow.setAlpha(alpha);
        backArrow.setScale(scale);

        reload.setAlpha(alpha);
        reload.setScale(scale);
    }

    @Override
    public void dispose() {
        oopsText.getTexture().dispose();
        failedSeal.getTexture().dispose();
        backArrow.getTexture().dispose();
        reload.getTexture().dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        oopsText.draw(spriteBatch);
        failedSeal.draw(spriteBatch);
        backArrow.draw(spriteBatch);
        reload.draw(spriteBatch);
    }

    private boolean soundPlayed=false;
    private int currentStep=0;
    public static final int STEP_SHOW_ANIMATION=0;
}
