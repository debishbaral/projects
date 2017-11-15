package nf.co.mohatram.hidingdot.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import nf.co.mohatram.game.Drawable;
import nf.co.mohatram.game.view.*;

public class Level implements Disposable, Drawable{
    private Array<ObjectView> visibleAreas, friendlyBarriers, deadlyBarriers, stars;
    private ObjectView player;
    private ObjectView finishArea;
    private LevelProperty levelProperty;
    private Camera camera;
    private Viewport viewport;
    private SpriteBatch spriteBatch;

    private Level(){
        visibleAreas=new Array<>();
        friendlyBarriers=new Array<>();
        deadlyBarriers=new Array<>();
        stars=new Array<>();
    }

    public static Level make(LevelProperty levelProperty,
                      Texture visibleArea,
                      Texture friendlyBarrier,
                      Texture deadlyBarrier,
                      Texture finishPointArea,
                      Texture star,
                      Texture playerNormal,
                      Texture playerHint){

        Level level=new Level();
        level.levelProperty=levelProperty;

        //create camera
        float   levelHeight=level.levelProperty.levelHeight,
                levelWidth=level.levelProperty.levelWidth;
        float   noOfRows=level.levelProperty.breakRow,//no of rows in which level is broken
                noOfCols=level.levelProperty.breakCol; //no of cols in which level is broken

        if (noOfCols<1) noOfCols=1;
        if (noOfRows<1) noOfRows=1;
        level.camera=new OrthographicCamera(levelWidth/noOfRows,
                                            levelHeight/noOfCols);
        level.camera.position.set(level.camera.viewportWidth/2f, level.camera.viewportHeight/2f, 0);

        //create viewport
        level.viewport=new FitViewport(1, 1, level.camera);
        level.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        //create visible areas
        for (ObjectProperty objectProperty:
                level.levelProperty.visibleAreaProperties) {
            level.visibleAreas.add(new VisibleArea(objectProperty, visibleArea));
        }

        //create friendly barriers
        for (ObjectProperty objectProperty:
                level.levelProperty.friendlyBarriersProperties) {
            level.friendlyBarriers.add(new FriendlyBarrier(objectProperty, friendlyBarrier));
        }

        //create deadly barriers
        for (ObjectProperty objectProperty:
                level.levelProperty.deadlyBarriersProperties) {
            level.deadlyBarriers.add(new DeadlyBarrier(objectProperty, deadlyBarrier));
        }

        //create finish point area
        level.finishArea =new FinishPointArea(level.levelProperty.finishPointAreaProperty, finishPointArea);

        //create stars
        for (ObjectProperty objectProperty:
                level.levelProperty.starsProperties) {
            level.stars.add(new Star(objectProperty, star));
        }

        //create player
        level.player=new Player(level.levelProperty.playerProperty, playerNormal, playerHint);
        return level;
    }


    @Override
    public void dispose() {

        //
        for (ObjectView objectView: visibleAreas){
            objectView.dispose();
        }

        //
        for (ObjectView objectView: friendlyBarriers){
            objectView.dispose();
        }

        //
        for (ObjectView objectView: deadlyBarriers){
            objectView.dispose();
        }

        //
        for (ObjectView objectView: stars){
            objectView.dispose();
        }

        player.dispose();
        finishArea.dispose();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
//
        for (ObjectView objectView: visibleAreas){
            objectView.draw(spriteBatch);
        }

        //
        for (ObjectView objectView: friendlyBarriers){
            objectView.draw(spriteBatch);
        }

        //
        for (ObjectView objectView: deadlyBarriers){
            objectView.draw(spriteBatch);
        }

        //
        for (ObjectView objectView: stars){
            objectView.draw(spriteBatch);
        }

        //
        player.draw(spriteBatch);
        //
        finishArea.draw(spriteBatch);
    }

    public void render() {
        this.spriteBatch.begin();
        draw(this.spriteBatch);
        this.spriteBatch.end();
    }

    public void resize(int width, int height) {
        float   levelHeight=levelProperty.levelHeight,
                levelWidth=levelProperty.levelWidth;
        float w, h;
        float aspectRatio=levelHeight/levelWidth;
        if (aspectRatio>1){
            w=width;
            h=height/aspectRatio;
        }else {
            h=height;
            w=width*aspectRatio;
        }
        viewport.update((int)w,(int)h);
        viewport.apply();
    }
}
