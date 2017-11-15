package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nf.co.mohatram.hidingdot.MusicService;


public class LevelController implements Disposable {
    private static final int controller_btns_height = 72;
    private static final int controller_btns_width = 72;
    private static final int chosen_screen_width = 800;
    private static final float padding = 24;
    public boolean leftPressed;
    public boolean downPressed;
    public boolean upPressed;
    public boolean rightPressed;
    public boolean reloadButtonPressed;
    public boolean visibilityPressed;
    private TextureRegion[][] split;
    private Texture texturePackage;
    public Stage stage;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch spriteBatch;
    private float scaleFactor;
    private Label labelNoOfHints;
    private Label pixelPosition;
    private BitmapFont font;
    /*public static final GlyphLayout glyphLayout=new GlyphLayout();*/


    Table controllersTable;
    Table menuTable;
    Table pixelTable;

    public LevelController() {
        spriteBatch = new SpriteBatch();
        initViewPort();
        initTexture();
        setUpActors();
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    private void setUpActors() {
        stage = new Stage(viewport, spriteBatch);
        controllersTable = new Table();
        controllersTable.setFillParent(true);
        controllersTable.left().bottom();


        final Drawable leftNormal = new TextureRegionDrawable(split[0][0]);
        final Drawable leftPressed = new TextureRegionDrawable(split[1][0]);

        final Drawable downNormal = new TextureRegionDrawable(split[0][1]);
        final Drawable downPressed = new TextureRegionDrawable(split[1][1]);

        final Drawable upNormal = new TextureRegionDrawable(split[0][2]);
        final Drawable upPressed = new TextureRegionDrawable(split[1][2]);

        final Drawable rightNormal = new TextureRegionDrawable(split[0][3]);
        final Drawable rightPressed = new TextureRegionDrawable(split[1][3]);

        final Image upButton = new Image(upNormal);
        final Image downButton = new Image(downNormal);
        final Image leftButton = new Image(leftNormal);
        final Image rightButton = new Image(rightNormal);

        //for up button
        upButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                Log.e("levelcontroller", "up pressed");
                upButton.setDrawable(upPressed);
                LevelController.this.upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upButton.setDrawable(upNormal);
                LevelController.this.upPressed = false;
            }
        });

        leftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                Log.e("levelcontroller", "left pressed");
                leftButton.setDrawable(leftPressed);
                LevelController.this.leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftButton.setDrawable(leftNormal);
                LevelController.this.leftPressed = false;
            }
        });

        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                Log.e("levelcontroller", "right pressed");
                rightButton.setDrawable(rightPressed);
                LevelController.this.rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //MusicService.get().playOnBtnClick();
                rightButton.setDrawable(rightNormal);
                LevelController.this.rightPressed = false;
            }
        });

        downButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                Log.e("levelcontroller", "down pressed");
                downButton.setDrawable(downPressed);
                LevelController.this.downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downButton.setDrawable(downNormal);
                LevelController.this.downPressed = false;
            }
        });


        controllersTable.row();
        controllersTable.add(leftButton).size(leftButton.getWidth() * scaleFactor, leftButton.getHeight() * scaleFactor).pad(0, padding * scaleFactor, padding * scaleFactor, padding * scaleFactor);
        controllersTable.add(upButton).size(upButton.getWidth() * scaleFactor, upButton.getHeight() * scaleFactor).pad(0, padding * scaleFactor, padding * scaleFactor, padding * scaleFactor).align(Align.left).expandX();

        controllersTable.add(downButton).size(downButton.getWidth() * scaleFactor, downButton.getHeight() * scaleFactor).pad(0, padding * scaleFactor, padding * scaleFactor, padding * scaleFactor).align(Align.right).expandX();
        controllersTable.add(rightButton).size(rightButton.getWidth() * scaleFactor, rightButton.getHeight() * scaleFactor).pad(0, padding * scaleFactor, padding * scaleFactor, padding * scaleFactor);

        stage.addActor(controllersTable);

        //setup additional menus
        final Drawable reloadImage = new TextureRegionDrawable(split[2][0]);
        final Drawable reloadImagePressed = new TextureRegionDrawable(split[2][1]);
        final Image reload = new Image(reloadImage);
        reload.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                Log.e("check_event", "Reload button pressed");
                if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
                reloadButtonPressed = true;
                reload.setDrawable(reloadImagePressed);
                reload.setOrigin(reload.getWidth() * 0.5f, reload.getHeight() * 0.5f);
                reload.rotateBy(-60);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                reloadButtonPressed = false;
                reload.setDrawable(reloadImage);
                reload.rotateBy(60);
            }
        });

        final Drawable visibilityNormal = new TextureRegionDrawable(split[2][2]);
        final Drawable visibilityPressed = new TextureRegionDrawable(split[2][3]);
        final Image visibility = new Image(visibilityNormal);
        visibility.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
                if (!LevelController.this.visibilityPressed){
                    LevelController.this.visibilityPressed = true;
                }
                visibility.setDrawable(visibilityPressed);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LevelController.this.visibilityPressed = false;
                visibility.setDrawable(visibilityNormal);
            }
        });


        menuTable = new Table();
        font=new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        labelNoOfHints=new Label("", new Label.LabelStyle(font, Color.GRAY));
        labelNoOfHints.setFontScale(2*scaleFactor);

        pixelPosition=new Label("", new Label.LabelStyle(font, Color.GRAY));
        pixelPosition.setFontScale(2*scaleFactor);

        menuTable.setFillParent(true);
        menuTable.top().left();
        menuTable.row();
        menuTable.add(reload).size(reload.getWidth() * scaleFactor / 1.5f, reload.getHeight() * scaleFactor / 1.5f).pad(padding * scaleFactor / 1.5f);
        menuTable.add(visibility).size(visibility.getWidth() * scaleFactor / 1.5f, visibility.getHeight() * scaleFactor / 1.5f).pad(padding * scaleFactor / 1.5f);
        menuTable.add(labelNoOfHints);


        pixelTable = new Table();
        pixelTable.setFillParent(true);
        pixelTable.top().right();
        pixelTable.row();
        pixelTable.add(pixelPosition).pad(padding * scaleFactor / 1.5f);

        stage.addActor(menuTable);
        stage.addActor(pixelTable);
    }

    private void initViewPort() {
        scaleFactor = ((float) Gdx.graphics.getWidth() / (float) chosen_screen_width);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply(true);
    }


    public void resize(int width, int height) {
        viewport.update(width, height);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void initTexture() {
        texturePackage = new Texture(Gdx.files.internal("graphics/controllers.png"), true);
        texturePackage.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.MipMapNearestNearest);
        split = TextureRegion.split(texturePackage, controller_btns_width, controller_btns_height);
//        Log.e("checking texture", "Texture loaded");
    }

    public void drawControls() {
        stage.draw();
    }

    @Override
    public void dispose() {
        texturePackage.dispose();
        spriteBatch.dispose();
    }

    public boolean isReloadButtonPressed() {
        return reloadButtonPressed;
    }

    public boolean isVisibilityPressed() {
        return visibilityPressed;
    }

    public void setVisibilityPressed(boolean visibilityPressed){this.visibilityPressed=visibilityPressed;}


    public void setNoOfHints(int noOfHints){
        labelNoOfHints.setText(noOfHints+"");
    }

    private StringBuffer stringBuffer=new StringBuffer();
    public void setPixelPosition(int x, int y){
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(x).append(" x ").append(y);
        pixelPosition.setText(stringBuffer.toString());
    }
    public enum Movement {
        UP, DOWN, LEFT, RIGHT
    }

    public void hide(){
        controllersTable.setVisible(false);
        menuTable.setVisible(false);
    }

    public void unHide(){
        controllersTable.setVisible(true);
        menuTable.setVisible(true);
    }
}
