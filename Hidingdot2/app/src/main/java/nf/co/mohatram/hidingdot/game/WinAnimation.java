package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import nf.co.mohatram.hidingdot.MusicService;

/**
 * Created by madan on 5/8/17.
 */

public abstract class WinAnimation extends CustomAnimation {
    private final int noOfStar;

    private CustomAccelerateInterpolator customAccelerateInterpolator = CustomAccelerateInterpolator.get();

    public CustomSprite nextArrow;
    private CustomSprite leftStar;
    private CustomSprite middleStar;
    private CustomSprite rightStar;
    private CustomSprite completedStamp;
    private Sprite completedText;


    private Texture sideStars;
    private float elapsedTime;

    public WinAnimation(float stepDuration, int noOfStar) {
        super(stepDuration);
        this.noOfStar = noOfStar;
        if (noOfStar>3 || noOfStar<1){
            throw  new RuntimeException("No of stars unexpected");
        }
        elapsedTime = 0;

        sideStars=new Texture(Gdx.files.internal("graphics/sidestar.png"), true);
        sideStars.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        middleStar= new CustomSprite(new Texture(Gdx.files.internal("graphics/middlestar.png"), true));
        completedStamp= new CustomSprite(new Texture(Gdx.files.internal("graphics/completeseal.png"), true));
        completedText= new Sprite(new Texture(Gdx.files.internal("graphics/completedtext.png"), true));
        nextArrow= new CustomSprite(new Texture(Gdx.files.internal("graphics/nextarrow.png"), true));

        particleEffect1=new ParticleEffect();
        particleEffect1.load(Gdx.files.internal("win_effect.effect"), Gdx.files.internal("graphics"));
        particleEffect1.getEmitters().first().setPosition(MathUtils.random(0, 800), MathUtils.random(0, 480));

        particleEffect2=new ParticleEffect();
        particleEffect2.load(Gdx.files.internal("win_effect.effect"), Gdx.files.internal("graphics"));
        particleEffect2.getEmitters().first().setPosition(MathUtils.random(0, 800), MathUtils.random(0, 480));

        leftStar= new CustomSprite(sideStars);
        rightStar= new CustomSprite(sideStars);


        leftStar.setBounds(176.916f, 178.622f, 147.597f, 141.751f);
        rightStar.setBounds(475.487f, 178.622f, 147.597f, 141.751f);
        middleStar.setBounds(315.789f, 184.622f, 168.421f, 161.751f);
        completedStamp.setBounds(259.936f, 105.261f, 111.548f, 110.681f);
        completedText.setBounds(186.650f, 364.381f, 426.699f, 29.629f);
        nextArrow.setBounds(350.245f, 41.301f, 99.510f, 84.749f);

        Gdx.input.setInputProcessor(this);
    }

    private int currentStep=0;
    @Override
    public void update(float dt) {
        elapsedTime= Math.min(elapsedTime+dt, getDuration());
        if (currentStep==STEP_SHOW_LEFT_STAR){
            showLeftStar();
        }else if (currentStep==STEP_SHOW_MIDDLE_STAR){
            if (noOfStar>1){
                showMiddleStar();
            }else {
                currentStep=STEP_SHOW_SEAL_AND_ARROW;
                elapsedTime=0;
            }
        }else if (currentStep==STEP_SHOW_RIGHT_STAR){
            if (noOfStar>2){
                showRightStar();
            }else {
                currentStep=STEP_SHOW_SEAL_AND_ARROW;
                elapsedTime=0;
            }
        }else if(currentStep==STEP_SHOW_SEAL_AND_ARROW){
            showSealAndArrow();

        }
        if (elapsedTime==getDuration()){
            elapsedTime=0;
            currentStep++;
        }

    }

    private void showSealAndArrow() {
        float alpha= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 0f, 1f);
        float scale= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 2f, 1f);
        completedStamp.setAlpha(alpha);
        completedStamp.setScale(scale);
        nextArrow.setAlpha(alpha);
        nextArrow.setScale(scale);
    }

    private void showRightStar() {
        if (noOfStarSoundPlayed<3){
            if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
            noOfStarSoundPlayed++;
        }
        float alpha= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 0f, 1f);
        float scale= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 2f, 1f);
        rightStar.setAlpha(alpha);
        rightStar.setScale(scale);
    }

    private void showMiddleStar() {
        if (noOfStarSoundPlayed<2){
            if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
            noOfStarSoundPlayed++;
        }
        float alpha= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 0f, 1f);
        float scale= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 2f, 1f);
        middleStar.setAlpha(alpha);
        middleStar.setScale(scale);
    }

    private void showLeftStar() {
        if (noOfStarSoundPlayed<1){
            if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
            noOfStarSoundPlayed++;
        }
        float alpha= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 0f, 1f);
        float scale= customAccelerateInterpolator.ofFloat(getDuration(), elapsedTime, 2f, 1f);
        leftStar.setAlpha(alpha);
        leftStar.setScale(scale);
    }

    @Override
    public void dispose() {
        sideStars.dispose();
        middleStar.getTexture().dispose();
        completedStamp.getTexture().dispose();
        completedText.getTexture().dispose();
        nextArrow.getTexture().dispose();

        particleEffect1.dispose();
        particleEffect2.dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        completedText.draw(spriteBatch);
        leftStar.draw(spriteBatch);
        rightStar.draw(spriteBatch);
        middleStar.draw(spriteBatch);
        completedStamp.draw(spriteBatch);
        nextArrow.draw(spriteBatch);
        particleEffect1.draw(spriteBatch);
        particleEffect1.update(Gdx.graphics.getDeltaTime());

        particleEffect2.draw(spriteBatch);
        particleEffect2.update(Gdx.graphics.getDeltaTime());

        if (particleEffect1.isComplete()){
            particleEffect1.reset();
            particleEffect1.getEmitters().first().setPosition(MathUtils.random(0, 800), MathUtils.random(0, 480));
        }
        if (particleEffect2.isComplete()){
            particleEffect2.reset();
            particleEffect2.getEmitters().first().setPosition(MathUtils.random(0, 800), MathUtils.random(0, 480));
        }
    }


    public static class CustomAccelerateInterpolator {
        private static CustomAccelerateInterpolator cs;
        public static CustomAccelerateInterpolator get(){
            if (cs==null)cs=new CustomAccelerateInterpolator();
            return cs;
        }

        private CustomAccelerateInterpolator(){}

        public float ofFloat(float totalDuration, float elapsedTime, float startValue, float endValue){
            float a=(endValue*endValue-startValue*startValue)/(2f*totalDuration);
            return (float) Math.sqrt(startValue*startValue+2f*a*elapsedTime);
        }
    }

    public static int STEP_SHOW_LEFT_STAR=0;
    public static int STEP_SHOW_MIDDLE_STAR=1;
    public static int STEP_SHOW_RIGHT_STAR=2;
    public static int STEP_SHOW_SEAL_AND_ARROW=3;

    private int noOfStarSoundPlayed =0;

    public ParticleEffect particleEffect1;
    public ParticleEffect particleEffect2;
}
