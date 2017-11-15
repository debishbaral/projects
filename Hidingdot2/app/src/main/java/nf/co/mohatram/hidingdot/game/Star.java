package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by madan on 5/5/17.
 */

public class Star extends ObjectView {
    public static final int view_type=2;
    private Texture texture=null;
    private final ParticleEffect particleEffect;

    public Star(ObjectProperty objectProperty, Texture texture, ParticleEffect particleEffect) {
        super(objectProperty);
        this.texture = texture;
        this.particleEffect = particleEffect;
    }

    @Override
    public int getType() {
        return view_type;
    }

    boolean particleEffectOn=false;
    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, objectProperty.position.x, objectProperty.position.y, objectProperty.dimension.x, objectProperty.dimension.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Star)) return false;
        if (this==obj) return true;
        Star star=(Star) obj;

        if (objectProperty.position.x==star.objectProperty.position.x &&
                objectProperty.position.y==star.objectProperty.position.y &&
                objectProperty.dimension.x==star.objectProperty.dimension.x &&
                objectProperty.dimension.y==star.objectProperty.dimension.y){
            return true;
        }
        return false;
    }

    public void  particleEffect(){
        particleEffectOn=true;
        particleEffect.getEmitters().first().setPosition(objectProperty.position.x, objectProperty.position.y);
        particleEffect.start();
    }
}
