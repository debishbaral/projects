package nf.co.mohatram.hidingdot.tutorial;

import android.content.Intent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.InputStream;

import nf.co.mohatram.hidingdot.DatabaseHelper;
import nf.co.mohatram.hidingdot.DotApplication;
import nf.co.mohatram.hidingdot.LevelSelectorActivity;
import nf.co.mohatram.hidingdot.MusicService;
import nf.co.mohatram.hidingdot.game.DeadlyBarrier;
import nf.co.mohatram.hidingdot.game.FinishArea;
import nf.co.mohatram.hidingdot.game.FriendlyBarrier;
import nf.co.mohatram.hidingdot.game.GameScreen;
import nf.co.mohatram.hidingdot.game.Level;
import nf.co.mohatram.hidingdot.game.LevelController;
import nf.co.mohatram.hidingdot.game.LevelProperty;
import nf.co.mohatram.hidingdot.game.ObjectView;
import nf.co.mohatram.hidingdot.game.Player;
import nf.co.mohatram.hidingdot.game.Star;

import static nf.co.mohatram.hidingdot.game.LevelController.Movement;


public class TutorialScreen implements Screen {
    public static final float ds = 100f;

    public SpriteBatch spriteBatch;
    public Viewport viewport;
    public OrthographicCamera orthographicCamera;
    public Level level;
    private LevelController levelController;
    private Vector2 initialPlayerPos;
    private Rectangle overLapRectangle;
    private Array<ObjectView> takenStars;

    private boolean gameWon = false;
    private boolean gameLost = false;
    private GameTutorial gameTutorial;


    public TutorialScreen(GameTutorial gameTutorial) {
        this.gameTutorial = gameTutorial;
        initialPlayerPos = new Vector2();
        overLapRectangle = new Rectangle();
        takenStars = new Array<>();
        currentTutorialState = TutorialState.tutorial;
        tutorialAnimation = new TutorialAnimation(1f);
    }


    private void loadLevel(String levelFile) {
        Json json = new Json();
        LevelProperty levelProperty;
        InputStream inputStream = Gdx.files.internal(levelFile).read();
        levelProperty = json.fromJson(LevelProperty.class, inputStream);
        level = Level.make(levelProperty);
        initialPlayerPos.set(levelProperty.playerProperty.position);
    }

    private void setupController() {
        levelController = new LevelController();
        Gdx.input.setInputProcessor(levelController.stage);
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        loadLevel("levels/tutorial.json");
        setUpCamera();
        setupController();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        levelController.resize(width, height);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(Player.player_color_normal.r, Player.player_color_normal.g, Player.player_color_normal.b, Player.player_color_normal.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.begin();
        level.draw(spriteBatch);
        if (currentTutorialState == TutorialState.tutorial) {
            tutorialAnimation.update(dt);
            tutorialAnimation.draw(spriteBatch);
        }
        spriteBatch.end();

        if (!gameWon && !gameLost) {
            update(dt);
            levelController.drawControls();
        } else {
            if (winLoseAnimation == null) {
                winLoseAnimation = new WinLoseAnimation(0.5f, gameWon) {
                    @Override
                    public boolean onTouch(int screenX, int screenY) {
                        if (gameWon) {
                            DatabaseHelper databaseHelper= DotApplication.get().getDatabaseHelper();
                            databaseHelper.setNoFirstTime();
                            Intent intent = new Intent(gameTutorial.tutorialActivity, LevelSelectorActivity.class);
                            gameTutorial.tutorialActivity.startActivity(intent);
                            gameTutorial.tutorialActivity.finish();
                            System.gc();
                        } else {
                            reload();
                        }
                        return true;
                    }
                };
            }
            spriteBatch.begin();
            winLoseAnimation.update(dt);
            winLoseAnimation.draw(spriteBatch);
            spriteBatch.end();
        }
    }

    public void reload() {
        Gdx.input.setInputProcessor(levelController.stage);
        currentTutorialState=TutorialState.tutorial;
        if (winLoseAnimation!=null){
            winLoseAnimation.dispose();
            winLoseAnimation = null;
        }

        if (tutorialAnimation!=null){
            tutorialAnimation.setCurrent_step(TutorialAnimation.STEP_INSIDE_VISIBLE_AREA);
        }
        currentTutorialState=TutorialState.tutorial;
        level.levelProperty.playerProperty.position.set(initialPlayerPos);
        for (ObjectView ov : takenStars) {
            level.stars.add(ov);
        }
        takenStars.clear();
        gameWon = false;
        gameLost = false;
        currentTutorialState = TutorialState.tutorial;
        disablePlayerGlow();
    }

    private void update(float deltaTime) {

        if (levelController.isReloadButtonPressed()) {
            reload();
        }
        if (levelController.isVisibilityPressed()) {
            enablePlayerGlow();
            if (tutorialAnimation.getCurrent_step()==TutorialAnimation.STEP_INVISIBLE_AREA){
                tutorialAnimation.setCurrent_step(TutorialAnimation.STEP_OTHERS);
            }
        }
        if (levelController.isUpPressed()) {

            level.levelProperty.playerProperty.position.add(0, (ds * deltaTime/* + dtUp * acceleration*/));
            disablePlayerGlow();
            handleCollision(Movement.UP);

        }
        if (levelController.isDownPressed()) {

            level.levelProperty.playerProperty.position.add(0, -(ds * deltaTime /*+ dtDown * acceleration*/));
            disablePlayerGlow();
            handleCollision(Movement.DOWN);

        }
        if (levelController.isLeftPressed()) {
            level.levelProperty.playerProperty.position.add(-(ds * deltaTime /*+ dtLeft * acceleration*/), 0);
            disablePlayerGlow();
            handleCollision(Movement.LEFT);
        }
        if (levelController.isRightPressed()) {

            level.levelProperty.playerProperty.position.add((ds * deltaTime /*+ dtRight * acceleration*/), 0);
            disablePlayerGlow();
            handleCollision(Movement.RIGHT);
        }

    }

    private void handleCollision(Movement m) {
        ObjectView collisionObject = checkForCollision();
        if (collisionObject == null) {
            //tutorialAnimation.restoreElapsedTime();
            tutorialAnimation.setCurrent_step(TutorialAnimation.STEP_INVISIBLE_AREA);
            return;
        }


        Rectangle collisionObjectRect = collisionObject.objectProperty.getBoundingRectangle();
        Rectangle playerRect = level.player.objectProperty.getBoundingRectangle();


        if (collisionObject.getType() == Star.view_type) {
            tutorialAnimation.setCurrent_step(TutorialAnimation.STEP_HIT_STAR);
            takenStars.add(collisionObject);
            level.stars.removeValue(collisionObject, false);
           if (MusicService.get()!=null)MusicService.get().cheerOnStar();
        }

        if (collisionObject.getType() == FinishArea.view_type) {
            if (MusicService.get()!=null)MusicService.get().cheerOnWin();
            ((Player) level.player).setGlowActive(true);
            currentTutorialState=TutorialState.no_tutorial;
            gameWon = true;
            gameLost = false;
        }

        if (collisionObject.getType() == DeadlyBarrier.view_type) {
            if (MusicService.get()!=null)MusicService.get().onDeadSoundEffect();
            ((Player) level.player).setGlowActive(true);
            currentTutorialState=TutorialState.no_tutorial;
            gameWon = false;
            gameLost = true;
        }

        if (collisionObject.getType() == FriendlyBarrier.view_type) {
            GameScreen.intersect(collisionObjectRect, playerRect, overLapRectangle);

            switch (m) {
                case UP: {
                    level.player.objectProperty.position.add(0, -overLapRectangle.height-1);
                    break;
                }
                case DOWN: {
                    level.player.objectProperty.position.add(0, overLapRectangle.height+1);
                    break;
                }
                case LEFT: {
                    level.player.objectProperty.position.add(overLapRectangle.width+1, 0);
                    break;
                }
                case RIGHT: {
                    level.player.objectProperty.position.add(-overLapRectangle.width-1, 0);
                    break;
                }
            }

            tutorialAnimation.setCurrent_step(TutorialAnimation.STEP_HIT_FRIENDLY_BARRIER);
            enablePlayerGlow();
        }
    }

    @Override
    public void pause() {
        MusicService.get().stopCheerOnWin();
    }

    @Override
    public void resume() {
        reload();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        level.dispose();
        levelController.dispose();
        spriteBatch.dispose();

        if (winLoseAnimation != null) {
            winLoseAnimation.dispose();
        }
        if (tutorialAnimation != null) {
            tutorialAnimation.dispose();
        }
    }

    private void setUpCamera() {
        float levelDisplayWidth = level.levelProperty.levelWidth / level.levelProperty.breakCol;
        float levelDisplayHeight = level.levelProperty.levelHeight / level.levelProperty.breakRow;
        orthographicCamera = new OrthographicCamera();
        viewport = new FitViewport(levelDisplayWidth, levelDisplayHeight, orthographicCamera);
        orthographicCamera.position.set(orthographicCamera.viewportWidth / 2f, orthographicCamera.viewportHeight / 2f, 0);
        orthographicCamera.update();
    }

    private void disablePlayerGlow() {
        ((Player) level.player).setGlowActive(false);
    }

    private void enablePlayerGlow() {
        ((Player) level.player).setGlowActive(true);
    }

    private ObjectView checkForCollision() {
        for (ObjectView ov : level.friendlyBarriers) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }

        for (ObjectView ov : level.deadlyBarriers) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }

        for (ObjectView ov : level.stars) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }
        for (ObjectView ov : level.visibleAreas) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }
        if (level.endArea.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
            return level.endArea;
        }
        return null;
    }

    public enum TutorialState {
        tutorial, no_tutorial
    }

    private WinLoseAnimation winLoseAnimation;
    private TutorialAnimation tutorialAnimation;
    private TutorialState currentTutorialState;
}
