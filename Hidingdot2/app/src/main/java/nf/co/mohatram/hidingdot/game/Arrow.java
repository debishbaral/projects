package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class Arrow extends ObjectView {

    private final Texture texture;
    private final Level level;
    private float rotation=0f;
    private boolean active;
    /*private boolean dx, dy;*/

    public Arrow(ObjectProperty objectProperty, Texture texture, Level level) {
        super(objectProperty);
        this.texture = texture;
        this.level = level;
        playerCenterVector=new Vector2();
    }

    /*public void calculate(Rectangle player, Rectangle screen){
        if (!screen.overlaps(player)){
            *//*float centerX=0, centerY=0;
            if (player.x > 0 && player.x<(screen.width-objectProperty.dimension.x)){
                centerX=player.x;
            }else if(player.x<=0){
                centerX=0;
            }else if (player.x>=(screen.width-objectProperty.dimension.x)){
                centerX=screen.width-objectProperty.dimension.x;
            }

            if (player.y > 0 && player.y<(screen.height-objectProperty.dimension.y)){
                centerY=player.y;
            }else if(player.y<=0){
                centerY=0;
            }else if (player.y>=(screen.height-objectProperty.dimension.y)){
                centerY=screen.height-objectProperty.dimension.y;
            }

            float py=centerY-objectProperty.dimension.y/2f;
            float px=centerX-objectProperty.dimension.x/2f;

            objectProperty.position.set(px, py);

            float rotation= (float) Math.atan((player.y-centerX)/(player.x-centerY));
            this.rotation= (float) (180.0f/Math.PI * rotation);*//*

            int currentPosition=GameScreen.getRegion(level.screen, level.player.objectProperty.position);


        }
    }*/

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (active)spriteBatch.draw(texture,
                objectProperty.position.x,
                objectProperty.dimension.y,
                texture.getWidth()/2f,
                texture.getHeight()/2f,
                objectProperty.dimension.x,
                objectProperty.dimension.y,
                1f,
                1f,
                rotation,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    public void setActive(boolean active) {
        if (active){
            int currentPartition=GameScreen.getRegion(level.screen, level.player.getObjectProperty().position);
            if (currentPartition!=-1){
                Vector2 playerCenter=getPlayerCenterVector();
                switch (currentPartition){
                    case 1:
                        rotation=135f;
                        objectProperty.position.set(0f, level.screen.height-objectProperty.dimension.y);
                        break;
                    case 2:
                        rotation=90f;
                        objectProperty.position.x=playerCenter.x-objectProperty.dimension.x/2f;
                        objectProperty.position.y=level.screen.height-objectProperty.dimension.y;
                        break;
                    case 3:
                        rotation=45f;
                        objectProperty.position.set(level.screen.width-objectProperty.dimension.x, level.screen.height-objectProperty.dimension.y);
                        break;
                    case 4:
                        rotation=180f;
                        objectProperty.position.x=0;
                        objectProperty.position.y=playerCenter.y-objectProperty.dimension.y/2f;
                        break;
                    case 6:
                        objectProperty.position.x=level.screen.width-objectProperty.dimension.x;
                        objectProperty.position.y=playerCenter.y-objectProperty.dimension.y/2f;
                        rotation=0f;
                        break;
                    case 7:
                        objectProperty.position.set(0f, 0f);
                        rotation=-135f;
                        break;
                    case 8:
                        objectProperty.position.x=playerCenter.x-objectProperty.dimension.x/2f;
                        objectProperty.position.y=0;
                        rotation=-90f;
                        break;
                    case 9:
                        rotation=-45f;
                        objectProperty.position.x=level.screen.width-objectProperty.dimension.x;
                        objectProperty.position.y=0;
                        break;
                }
            }
        }
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    private Vector2 playerCenterVector;

    public Vector2 getPlayerCenterVector() {
        playerCenterVector.set(level.player.getObjectProperty().position.add(level.player.getObjectProperty().dimension.x/2f,level.player.getObjectProperty().dimension.x/2f ));
        return playerCenterVector;
    }
}
