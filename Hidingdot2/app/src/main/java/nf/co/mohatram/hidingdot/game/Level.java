package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Level implements Disposable, Drawable {
    public Array<ObjectView> visibleAreas;
    public Array<ObjectView> friendlyBarriers;
    public Array<ObjectView> stars;
    public Array<ObjectView> deadlyBarriers;

    public ObjectView player;
    public ObjectView endArea;
    public LevelProperty levelProperty;

    public Border right, left, top, down;

   /* public Arrow arrowPointer;*/

    public Rectangle screen;



    private Level() {
        visibleAreas = new Array<>();
        friendlyBarriers = new Array<>();
        stars = new Array<>();
        deadlyBarriers = new Array<>();
        screen=new Rectangle();
    }

    public static Level make(LevelProperty levelProperty) {
        Level level = new Level();
        level.screen.set(0, 0, levelProperty.levelWidth, levelProperty.levelHeight);
        level.levelProperty=levelProperty;
        level.initTexture();
        //deadly barriers
        for (ObjectProperty op: levelProperty.deadlyBarriersProperties){level.deadlyBarriers.add(new DeadlyBarrier(op, level.deadlyBarrier));}

        //friendly barriers
        for (ObjectProperty op: levelProperty.friendlyBarriersProperties){level.friendlyBarriers.add(new FriendlyBarrier(op, level.friendlyBarrier));}

        //stars
        for (ObjectProperty op: levelProperty.starsProperties){level.stars.add(new Star(op, level.star, level.particleEffect));}

        //visible areas
        for (ObjectProperty op: levelProperty.visibleAreaProperties){level.visibleAreas.add(new VisibleArea(op, level.visibleBox));}

        //player
        level.player=new Player(levelProperty.playerProperty, level.playerTextureNormal, level.playerTextureGlow);

        //end area
        level.endArea=new FinishArea(levelProperty.finishPointAreaProperty, level.endBox);

        //right border property
        ObjectProperty rightBorderProperty=new ObjectProperty();
        rightBorderProperty.dimension.set(level.borderRight.getWidth(), level.borderRight.getHeight());
        rightBorderProperty.position.set(level.screen.width-rightBorderProperty.dimension.x, 0);
        level.right=new Border(rightBorderProperty, level.borderRight);

        ObjectProperty leftBorderProperty=new ObjectProperty();
        leftBorderProperty.dimension.set(level.borderLeft.getWidth(), level.borderLeft.getHeight());
        leftBorderProperty.position.set(0, 0);
        level.left=new Border(leftBorderProperty, level.borderLeft);

        ObjectProperty topBorderProperty=new ObjectProperty();
        topBorderProperty.dimension.set(level.borderTop.getWidth(), level.borderTop.getHeight());
        topBorderProperty.position.set(0, level.screen.height-topBorderProperty.dimension.y);
        level.top=new Border(topBorderProperty, level.borderTop);

        ObjectProperty bottomBorderProperty=new ObjectProperty();
        bottomBorderProperty.dimension.set(level.borderBottom.getWidth(), level.borderBottom.getHeight());
        bottomBorderProperty.position.set(0, 0);
        level.down=new Border(bottomBorderProperty, level.borderBottom);

        return level;
    }

    @Override
    public void dispose() {
        playerTextureNormal.dispose();
        playerTextureGlow.dispose();
        visibleBox.dispose();
        friendlyBarrier.dispose();
        deadlyBarrier.dispose();
        endBox.dispose();
        star.dispose();
        borderTop.dispose();
        borderLeft.dispose();
        borderBottom.dispose();
        borderRight.dispose();
    }


    @Override
    public void draw(SpriteBatch spriteBatch) {
        //Log.e("FPS CHeck", ""+ Gdx.graphics.getFramesPerSecond());
        for (ObjectView ov : visibleAreas) {ov.draw(spriteBatch);}
        for (ObjectView ov : friendlyBarriers) {ov.draw(spriteBatch);}
        for (ObjectView ov : deadlyBarriers) {ov.draw(spriteBatch);}
        for (ObjectView ov : stars) {ov.draw(spriteBatch);}
        endArea.draw(spriteBatch);
        player.draw(spriteBatch);

        top.draw(spriteBatch);
        down.draw(spriteBatch);
        right.draw(spriteBatch);
        left.draw(spriteBatch);

   /*     arrowPointer.draw(spriteBatch);*/
    }

    private void initTexture(){
        Pixmap pixmap;
        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(Player.player_color_normal);
        pixmap.fill();
        playerTextureNormal =new Texture(pixmap, true);
        playerTextureNormal.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(Player.player_color_glow);
        pixmap.fill();
        playerTextureGlow=new Texture(pixmap, true);
        playerTextureGlow.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(VisibleArea.visible_area_color);
        pixmap.fill();
        visibleBox=new Texture(pixmap, true);
        visibleBox.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(FriendlyBarrier.friendly_barrier_color);
        pixmap.fill();
        friendlyBarrier=new Texture(pixmap, true);
        friendlyBarrier.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(DeadlyBarrier.deadly_barrier_color);
        pixmap.fill();
        deadlyBarrier=new Texture(pixmap, true);
        deadlyBarrier.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        pixmap=new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        pixmap.setColor(FinishArea.finish_color_area);
        pixmap.fill();
        endBox=new Texture(pixmap, true);
        endBox.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        pixmap.dispose();

        star=new Texture(Gdx.files.internal("graphics/star24.png"), true);
        star.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);

        pixmap =new Pixmap(Gdx.files.internal("graphics/topborder.png"));
        borderTop=new Texture(pixmap);
        pixmap.dispose();

        pixmap =new Pixmap(Gdx.files.internal("graphics/bottom.png"));
        borderBottom=new Texture(pixmap);
        pixmap.dispose();

        pixmap =new Pixmap(Gdx.files.internal("graphics/left.png"));
        borderLeft=new Texture(pixmap);
        pixmap.dispose();

        pixmap=new Pixmap(Gdx.files.internal("graphics/right.png"));
        borderRight=new Texture(pixmap);
        pixmap.dispose();

        particleEffect=new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particle_effect"), Gdx.files.internal("graphics"));

    }

    private Texture playerTextureNormal;
    private Texture playerTextureGlow;
    private Texture visibleBox;
    private Texture friendlyBarrier;
    private Texture deadlyBarrier;
    private Texture endBox;
    private Texture star;
    private Texture borderTop;
    private Texture borderBottom;
    private Texture borderRight;
    private Texture borderLeft;
    public ParticleEffect particleEffect;
}
