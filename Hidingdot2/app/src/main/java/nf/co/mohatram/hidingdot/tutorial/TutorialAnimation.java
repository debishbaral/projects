package nf.co.mohatram.hidingdot.tutorial;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nf.co.mohatram.hidingdot.game.CustomAnimation;
import nf.co.mohatram.hidingdot.game.WinAnimation;

/**
 * Created by madan on 5/10/17.
 */

public class TutorialAnimation extends CustomAnimation {
    private Sprite tutorialTextVisibleArea;
    private Sprite tutorialTextInvisibleArea;
    private Sprite tutorialTextHitFriendlyBarrier;
    private Sprite tutorialTextHitStar;

    private Sprite downPointingHand;
    private Sprite rightPointingHand;

    private float xd, yd;
    private float xr, yr;
    private float elapsedTime=0;

    public TutorialAnimation(float duration){
        super(duration);
        tutorialTextVisibleArea =new Sprite(new Texture(Gdx.files.internal("tutorial/tutorial_inside_vis.png"), true));
        downPointingHand=new Sprite(new Texture(Gdx.files.internal("tutorial/handdown.png"), true));
        rightPointingHand=new Sprite(new Texture(Gdx.files.internal("tutorial/handright.png"), true));
        tutorialTextHitStar=new Sprite(new Texture(Gdx.files.internal("tutorial/tut_star.png"), true));
        tutorialTextHitFriendlyBarrier=new Sprite(new Texture(Gdx.files.internal("tutorial/tut_green_barr.png"), true));
        tutorialTextInvisibleArea=new Sprite(new Sprite(new Texture(Gdx.files.internal("tutorial/tut_invisible.png"), true)));

        tutorialTextVisibleArea.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        downPointingHand.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        rightPointingHand.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        tutorialTextHitStar.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        tutorialTextHitFriendlyBarrier.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        tutorialTextInvisibleArea.getTexture().setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);

        tutorialTextVisibleArea.setBounds(10f, 200f, 472f, 160f);
        downPointingHand.setBounds(14f, 160f,38f, 56f );
        rightPointingHand.setBounds(680f, 166f, 56f, 38f);
        tutorialTextInvisibleArea.setBounds(20f, 190f, 788f, 212f);
        tutorialTextHitFriendlyBarrier.setBounds(326f, 236f, 472f, 160f);
        tutorialTextHitStar.setBounds(336f, 200f, 354f, 128f);

        xd=downPointingHand.getX(); yd=downPointingHand.getY();
        xr=rightPointingHand.getX(); yr=rightPointingHand.getY();
    }
    public void update(float dt){
        elapsedTime+=dt;
        float dp= (float) CustomSineInterpolator.get().ofDouble(getDuration(), elapsedTime, 5f);

        if(elapsedTime<getDuration()){
            float alpha = WinAnimation.CustomAccelerateInterpolator.get().ofFloat(getDuration(), elapsedTime, 0f, 1f);
            if(current_step==STEP_INSIDE_VISIBLE_AREA) {
                tutorialTextVisibleArea.setAlpha(alpha);
            }else if (current_step==STEP_INVISIBLE_AREA){
                tutorialTextInvisibleArea.setAlpha(alpha);
            }else if (current_step==STEP_HIT_FRIENDLY_BARRIER){

                Log.e("elapsed_", ""+elapsedTime);
                tutorialTextHitFriendlyBarrier.setAlpha(alpha);
            }else if (current_step==STEP_HIT_STAR){
                tutorialTextHitStar.setAlpha(alpha);
            }
        }
        if (current_step==STEP_HIT_FRIENDLY_BARRIER ){
            if (elapsedTime>10f) {
                current_step=STEP_OTHERS;
            }
        }
        if (current_step==STEP_HIT_STAR){
            if (elapsedTime>5f){
                current_step=STEP_OTHERS;
            }
        }
        downPointingHand.setPosition(xd, yd+dp);
        rightPointingHand.setPosition(xr+dp, yr);
    }

    public void draw(SpriteBatch spriteBatch){
        if (current_step==STEP_INSIDE_VISIBLE_AREA){
            tutorialTextVisibleArea.draw(spriteBatch);
            downPointingHand.draw(spriteBatch);
        }if (current_step==STEP_INVISIBLE_AREA){
            tutorialTextInvisibleArea.draw(spriteBatch);
        }if (current_step==STEP_HIT_FRIENDLY_BARRIER){
            tutorialTextHitFriendlyBarrier.draw(spriteBatch);
        }if (current_step==STEP_HIT_STAR){
            tutorialTextHitStar.draw(spriteBatch);
        }
        rightPointingHand.draw(spriteBatch);
    }

    public void dispose(){
        tutorialTextVisibleArea.getTexture().dispose();
        downPointingHand.getTexture().dispose();
        rightPointingHand.getTexture().dispose();
        tutorialTextHitFriendlyBarrier.getTexture().dispose();
        tutorialTextHitStar.getTexture().dispose();
        tutorialTextInvisibleArea.getTexture().dispose();
    }

    @Override
    public boolean onTouch(int screenX, int screenY) {
        return false;
    }


    public void setCurrent_step(int current_step) {
        if (current_step==STEP_INVISIBLE_AREA){
            if(this.current_step>current_step) return;
        }
        if (this.current_step!=current_step){
            restoreElapsedTime();
        }
        this.current_step = current_step;

    }

    public static class CustomSineInterpolator {
        private static CustomSineInterpolator csi;
        public static CustomSineInterpolator get() {
            if (csi==null){
                csi=new CustomSineInterpolator();
            }
            return csi;
        }

        public double ofDouble(float duration, float elapsedTime, float amplitude) {
           return amplitude*Math.sin(2*3.14/duration*elapsedTime);
        }
    }

    public int getCurrent_step() {
        return current_step;
    }

    private int current_step;

    public static final int STEP_INSIDE_VISIBLE_AREA=0;
    public static final int STEP_INVISIBLE_AREA=1;
    public static final int STEP_HIT_FRIENDLY_BARRIER=2;
    public static final int STEP_HIT_STAR=3;
    public static final int STEP_OTHERS=4;


    private void restoreElapsedTime(){
        elapsedTime=0;
    }

}

