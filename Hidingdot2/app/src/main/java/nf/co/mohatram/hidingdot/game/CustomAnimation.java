package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.InputAdapter;

/**
 * Created by madan on 5/8/17.
 */

public abstract class CustomAnimation extends InputAdapter implements Drawable {
    private float duration;

    public CustomAnimation(float duration){
        this.duration = duration;
    }
    public abstract void update(float dt);

    public float getDuration() {
        return duration;
    }

    public abstract void dispose();


    public abstract boolean onTouch(int screenX, int screenY);

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return onTouch(screenX, screenY);
    }
}
