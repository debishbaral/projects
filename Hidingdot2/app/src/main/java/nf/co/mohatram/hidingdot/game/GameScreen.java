package nf.co.mohatram.hidingdot.game;

import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.InputStream;

import nf.co.mohatram.hidingdot.MusicService;

import static nf.co.mohatram.hidingdot.game.LevelController.Movement;


public class GameScreen extends GestureDetector.GestureAdapter implements Screen {
    public static final float ds = 90f; //virtual pixels in 1sec
    /*public static final float acceleration = 0f;*/
    public final HidingDotGame hidingDotGame;

    public SpriteBatch spriteBatch;
    public Viewport viewport;
    public OrthographicCamera orthographicCamera;
    public Level level;
    private String levelFile;
    private LevelController levelController;
    private Vector2 initialPlayerPos;
    private Rectangle overLapRectangle;
    private Array<ObjectView> takenStars;

    private boolean gameWon = false;
    private boolean gameLost = false;
    private int noOfStarsFound = 0;
    private boolean taskAlreadyScheduled = false;

    private boolean dotPreviouslyOutofScreen=false;

    public GameScreen(HidingDotGame hidingDotGame) {
        this.hidingDotGame = hidingDotGame;
        this.levelFile = hidingDotGame.gameActivity.levelModel.levelFileName;
        initialPlayerPos = new Vector2();
        overLapRectangle = new Rectangle();
        takenStars = new Array<>();
        pixelPosition=new Vector3();
    }

    public static boolean intersect(Rectangle a, Rectangle b, Rectangle overlapRegion) {
        if (a.overlaps(b)) {
            overlapRegion.x = Math.max(a.x, b.x);
            overlapRegion.y = Math.max(a.y, b.y);
            overlapRegion.width = Math.min(a.x + a.width, b.x + b.width) - overlapRegion.x;
            overlapRegion.height = Math.min(a.y + a.height, b.y + b.height) - overlapRegion.y;
            return true;
        } else {
            return false;
        }
    }

    private void loadLevel(String levelFile) {
       /* Log.e("check______", levelFile);*/
        Json json = new Json();
        LevelProperty levelProperty;
        InputStream inputStream = Gdx.files.internal(levelFile).read();
        levelProperty = json.fromJson(LevelProperty.class, inputStream);
        level = Level.make(levelProperty);
        initialPlayerPos.set(levelProperty.playerProperty.position);
    }

    private void setupController() {
        levelController = new LevelController();
        levelController.setNoOfHints(hidingDotGame.databaseHelper.getNoOfHints());
        levelController.unHide();
        Gdx.input.setInputProcessor(levelController.stage);
    }


    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        loadLevel(levelFile);
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
        if (particleEffectOn){
            level.particleEffect.draw(spriteBatch);
            level.particleEffect.update(dt);
            if (level.particleEffect.isComplete()){
                level.particleEffect.reset();
                particleEffectOn=false;
            }
        }
        spriteBatch.end();

        if (!gameWon && !gameLost) {
            update(dt);
        }else {
            levelController.hide();
        }
        levelController.drawControls();
    }

    public void reload() {
        Gdx.input.setInputProcessor(levelController.stage);
        levelController.unHide();
        noOfStarsFound = 0;
        level.levelProperty.playerProperty.position.set(initialPlayerPos);
        for (ObjectView ov : takenStars) {
            level.stars.add(ov);
        }
        taskAlreadyScheduled = false;
        takenStars.clear();
        gameWon = false;
        gameLost = false;
        enablePlayerGlow();
    }

    private Vector3 pixelPosition;
    private void update(float deltaTime) {

        if (!level.screen.overlaps(level.player.objectProperty.getBoundingRectangle())) {
            if (!dotPreviouslyOutofScreen){
                hidingDotGame.gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(hidingDotGame.gameActivity, "Dot is out of screen.", Toast.LENGTH_SHORT).show();
                    }
                });
                dotPreviouslyOutofScreen=true;
            }
            int cureentPartition = getRegion(level.screen, level.player.objectProperty.position);
            if (cureentPartition != -1) {
                switch (cureentPartition) {
                    case 1:
                        turnOnBorders(true, false, false, true);
                        break;
                    case 2:
                        turnOnBorders(true, false, false, false);
                        break;
                    case 3:
                        turnOnBorders(true, true, false, false);
                        break;
                    case 4:
                        turnOnBorders(false, false, false, true);
                        break;
                    case 6:
                        turnOnBorders(false, true, false, false);
                        break;
                    case 7:
                        turnOnBorders(false, false, true, true);
                        break;
                    case 8:
                        turnOnBorders(false, false, true, false);
                        break;
                    case 9:
                        turnOnBorders(false, true, true, false);
                        break;
                }
            }
        } else {
            turnOnBorders(false, false, false, false);
            dotPreviouslyOutofScreen=false;
        }
        if (levelController.isReloadButtonPressed()) {
            reload();
        }

        if (levelController.isVisibilityPressed()) {
            onVisibilityPressed();
        }
        if (levelController.isUpPressed()) {
            level.levelProperty.playerProperty.position.add(0, (ds * deltaTime/* + dtUp * acceleration*/));
            disableHints();
            handleCollision(Movement.UP);
            updatePlayerPosition();

        } else if (levelController.isDownPressed()) {
            level.levelProperty.playerProperty.position.add(0, -(ds * deltaTime /*+ dtDown * acceleration*/));
            disableHints();
            handleCollision(Movement.DOWN);
            updatePlayerPosition();
        }
        if (levelController.isLeftPressed()) {
            level.levelProperty.playerProperty.position.add(-(ds * deltaTime /*+ dtLeft * acceleration*/), 0);
            disableHints();
            handleCollision(Movement.LEFT);
            updatePlayerPosition();
        }
        if (levelController.isRightPressed()) {
            level.levelProperty.playerProperty.position.add((ds * deltaTime /*+ dtRight * acceleration*/), 0);
            disableHints();
            handleCollision(Movement.RIGHT);
            updatePlayerPosition();
        }

    }

    private void updatePlayerPosition() {
        pixelPosition.x=level.player.objectProperty.position.x+level.player.objectProperty.dimension.x/2f;
        pixelPosition.y=level.player.objectProperty.position.y+level.player.objectProperty.dimension.y/2f;
        pixelPosition.z=0;
        orthographicCamera.project(pixelPosition);

        levelController.setPixelPosition((int)pixelPosition.x, (int)pixelPosition.y);
    }


    private void onVisibilityPressed() {
        levelController.setVisibilityPressed(false);

        final int noOfHints = hidingDotGame.databaseHelper.getNoOfHints();

        if(noOfHints>0) {
            if (!level.screen.overlaps(level.player.getObjectProperty().getBoundingRectangle())) {
                /*if (!level.arrowPointer.isActive()) {
                    level.arrowPointer.setActive(true);
                    hidingDotGame.databaseHelper.addNoOfHints(-1);
                }*/
                hidingDotGame.gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(hidingDotGame.gameActivity, "Dot is out of screen.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                if (!((Player) level.player).isGlowActive()) {
                    enablePlayerGlow();
                    hidingDotGame.databaseHelper.addNoOfHints(-1);
                }
            }
        }else {
            hidingDotGame.gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(hidingDotGame.gameActivity, "No more hints. Share with facebook to get more.", Toast.LENGTH_LONG).show();
                }
            });
        }
        levelController.setNoOfHints(hidingDotGame.databaseHelper.getNoOfHints());
    }

    private void handleCollision(Movement m) {
        ObjectView collisionObject = checkForCollision();
        if (collisionObject == null) return;


        Rectangle collisionObjectRect = collisionObject.objectProperty.getBoundingRectangle();
        Rectangle playerRect = level.player.objectProperty.getBoundingRectangle();


        //check if it is star
        if (collisionObject.getType() == Star.view_type) {
            /*hidingDotGame.gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(hidingDotGame.gameActivity, "Star found", Toast.LENGTH_SHORT).show();
                }
            });*/
            //remove star
            noOfStarsFound++;
            takenStars.add(collisionObject);
            level.stars.removeValue(collisionObject, false);
            if (MusicService.get() != null) MusicService.get().cheerOnStar();
            startParticleEffect(collisionObject.objectProperty.position.x, collisionObject.objectProperty.position.y);

            /*Log.e("no_of_stars", "..." + noOfStarsFound);*/
        }


        //check if it is end box
        if (collisionObject.getType() == FinishArea.view_type) {
            //you won
            if (MusicService.get() != null) MusicService.get().cheerOnWin();
            ((Player) level.player).setGlowActive(true);
            noOfStarsFound++;
            gameWon = true;
            gameLost = false;
            //show winning message
        }

        //check if it is deadly barrier
        if (collisionObject.getType() == DeadlyBarrier.view_type) {
            if (MusicService.get() != null) MusicService.get().onDeadSoundEffect();
            ((Player) level.player).setGlowActive(true);
            gameWon = false;
            gameLost = true;
        }

        //check if it is friendly barrier
        if (collisionObject.getType() == FriendlyBarrier.view_type) {
            intersect(collisionObjectRect, playerRect, overLapRectangle);

            switch (m) {
                case UP: {
                    level.player.objectProperty.position.add(0, -overLapRectangle.height);
                    break;
                }
                case DOWN: {
                    level.player.objectProperty.position.add(0, overLapRectangle.height);
                    break;
                }
                case LEFT: {
                    level.player.objectProperty.position.add(overLapRectangle.width, 0);
                    break;
                }
                case RIGHT: {
                    level.player.objectProperty.position.add(-overLapRectangle.width, 0);
                    break;
                }
            }
        }
        if ((gameWon || gameLost) && !taskAlreadyScheduled) {
            taskAlreadyScheduled = true;
            /*Log.e("check timer", "Timer is scheduled");*/
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    /*Log.e("check timer", "Timer is running");*/
                    hidingDotGame.setScreen(new EndingScreen(hidingDotGame, gameWon, noOfStarsFound));
                }
            }, 1.5f);
        }
    }

    @Override
    public void pause() {
        if (MusicService.get() != null) MusicService.get().stopCheerOnWin();
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
    }

    private float levelDisplayHeight;
    private float levelDisplayWidth;

    private  boolean particleEffectOn=false;
    private void startParticleEffect(float x, float y){
        level.particleEffect.getEmitters().first().setPosition(x, y);
        level.particleEffect.start();
        particleEffectOn=true;
    }
    private void setUpCamera() {
        levelDisplayWidth = level.levelProperty.levelWidth / level.levelProperty.breakCol;
        levelDisplayHeight = level.levelProperty.levelHeight / level.levelProperty.breakRow;
        orthographicCamera = new OrthographicCamera();
        viewport = new FitViewport(levelDisplayWidth, levelDisplayHeight, orthographicCamera);
        orthographicCamera.position.set(orthographicCamera.viewportWidth / 2f, orthographicCamera.viewportHeight / 2f, 0);
        orthographicCamera.update();
    }

    private void disableHints() {
        ((Player) level.player).setGlowActive(false);
        /*level.arrowPointer.setActive(false);*/
    }

    private void enablePlayerGlow() {
        ((Player) level.player).setGlowActive(true);
    }

    private ObjectView checkForCollision() {
        //collision with friendly barriers
        for (ObjectView ov : level.friendlyBarriers) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }

        //collision with deadly barriers
        for (ObjectView ov : level.deadlyBarriers) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }

        //collision with stars
        for (ObjectView ov : level.stars) {
            if (ov.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
                return ov;
            }
        }

        //collision with visible area
        if (level.endArea.objectProperty.getBoundingRectangle().overlaps(level.player.objectProperty.getBoundingRectangle())) {
            return level.endArea;
        }
        return null;
    }


    /*public static boolean isRectInside(Rectangle small, Rectangle big){
        return big.contains(small);
    }*/
    /*public static void getCenter(Vector2 a, Vector2 b, Vector2 center) {
        center.x = (a.x + b.x) / 2f;
        center.y = (a.y + b.y) / 2f;
    }

    public static float getDistance(Vector2 a, Vector2 b) {
        return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    *//*private boolean blinkOn = true;*//*
    private float elapsedTime = 0;*/

    public static int getRegion(Rectangle screen, Vector2 point) {
        if (screen.contains(point)) return 5;

        if (point.y > 0 && point.y < screen.height) {
            if (point.x < 0) {
                return 4;
            } else {
                return 6;
            }
        }

        if (point.y < 0) {
            if (point.x > 0 && point.x < screen.width) {
                return 8;
            } else if (point.x < 0) {
                return 7;
            } else {
                return 9;
            }
        }

        if (point.y > screen.height) {
            if (point.x > 0 && point.x < screen.width) {
                return 2;
            } else if (point.x < 0) {
                return 1;
            } else {
                return 3;
            }
        }

        return -1;
    }

    public void turnOnBorders(boolean top, boolean right, boolean down, boolean left) {
        level.top.setActive(top);
        level.right.setActive(right);
        level.down.setActive(down);
        level.left.setActive(left);
    }
}
