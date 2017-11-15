package nf.co.mohatram.hidingdot.game;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nf.co.mohatram.hidingdot.DatabaseHelper;
import nf.co.mohatram.hidingdot.GameActivity;
import nf.co.mohatram.hidingdot.MusicService;
import nf.co.mohatram.hidingdot.Setting;


public class EndingScreen implements Screen {
    private final HidingDotGame hidingDotGame;
    private final boolean gameWon;
    private final int noOfStarsFound;
    private DatabaseHelper databaseHelper;
    private Viewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private Level hidingDotLevel;
    private CustomAnimation customAnimation;

    public EndingScreen(HidingDotGame hidingDotGame, boolean gameWon, int noOfStarsFound) {
        /*Log.e("render", "Dont know what the problem is");*/
        this.hidingDotGame = hidingDotGame;
        this.gameWon = gameWon;
        /*Log.e("gamewon",gameWon+"");*/
        this.noOfStarsFound = noOfStarsFound;
        this.databaseHelper = hidingDotGame.databaseHelper;
        setUpDatabase();
    }

    private void setUpCamera() {
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(hidingDotLevel.levelProperty.levelWidth / hidingDotLevel.levelProperty.breakCol,
                hidingDotLevel.levelProperty.levelHeight / hidingDotLevel.levelProperty.breakRow, camera);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
    }

    private void setUpDatabase() {
        if (gameWon) {
            int levelId = hidingDotGame.gameActivity.levelModel.levelId;
            if (noOfStarsFound> hidingDotGame.gameActivity.levelModel.noOfStars) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.col_no_of_stars, noOfStarsFound);
                databaseHelper.update(levelId, contentValues);

                contentValues.clear();
                contentValues.put(DatabaseHelper.col_locked, false);
                /*contentValues.put(DatabaseHelper.col_no_of_stars, 0);*/
                databaseHelper.update(levelId + 1, contentValues);

                if (hidingDotGame.gameActivity.levelModel.noOfStars==0){
                    if (noOfStarsFound==1){
                        databaseHelper.addNoOfHints(Setting.noOfHintsOn1Star);
                    }else if(noOfStarsFound==2){
                        databaseHelper.addNoOfHints(Setting.noOfHintsOn2Star);
                    }else if (noOfStarsFound==3){
                        databaseHelper.addNoOfHints(Setting.noOfHintsOn3Star);
                    }
                }
            }
        }
    }

    @Override
    public void show() {
        setUPDisplay();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.setColor(0.3f, 0.3f, 0.3f, 1f);
        hidingDotLevel.draw(spriteBatch);
        spriteBatch.setColor(Color.WHITE);
        customAnimation.update(delta);
        customAnimation.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if(MusicService.get()!=null)MusicService.get().stopCheerOnWin();
    }

    @Override
    public void dispose() {
        customAnimation.dispose();
    }

    private void setUPDisplay(){
        this.spriteBatch = hidingDotGame.gameScreen.spriteBatch;
        this.hidingDotLevel = hidingDotGame.gameScreen.level;
        setUpCamera();

        if (gameWon) {
            customAnimation = new WinAnimation(0.5f, noOfStarsFound) {
                @Override
                public boolean onTouch(int screenX, int screenY) {
                    v3.x=screenX;
                    v3.y=screenY;
                    camera.unproject(v3);
                    if(nextArrow.getBoundingRectangle().contains(v3.x, v3.y)){
                        onNextLevel();
                    }
                    return true;
                }
            };
        } else {
            customAnimation = new LoseAnimation(0.5f) {
                @Override
                public boolean onTouch(int screenX, int screenY) {
                    v3.x=screenX;
                    v3.y=screenY;
                    camera.unproject(v3);
                    if (backArrow.getBoundingRectangle().contains(v3.x, v3.y)){
                        onBackArrowPressed();
                    }else if(reload.getBoundingRectangle().contains(v3.x, v3.y)){
                        onReloadPressed();
                    }
                    return true;
                }
            };
        }
    }

    private void onReloadPressed() {
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        hidingDotGame.gameScreen.reload();
        hidingDotGame.setScreen(hidingDotGame.gameScreen);
        this.dispose();
    }

    private void onBackArrowPressed() {
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        Intent intent=new Intent();
        intent.putExtra(key_this_level, hidingDotGame.gameActivity.levelModel.levelId);
        intent.putExtra(key_level_won, gameWon);
        intent.putExtra(key_no_of_stars, noOfStarsFound);
        hidingDotGame.gameActivity.setResult(Activity.RESULT_OK, intent);
        this.dispose();
        hidingDotGame.gameActivity.finish();
    }

    private void onNextLevel() {
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        GameActivity gameActivity = hidingDotGame.gameActivity;
        Intent intent=new Intent();
        intent.putExtra(key_this_level, hidingDotGame.gameActivity.levelModel.levelId);
        intent.putExtra(key_level_won, gameWon);
        intent.putExtra(key_no_of_stars, noOfStarsFound);
        gameActivity.setResult(Activity.RESULT_OK, intent);
        this.dispose();
        hidingDotGame.gameActivity.finish();
    }

    public final Vector3 v3=new Vector3();
    public static final String key_this_level="ad34908ca1";
    public static final String key_level_won="1cafe798f3";
    public static final String key_no_of_stars="adfe34cb12a";
}