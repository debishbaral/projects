package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.utils.Disposable;
import nf.co.mohatram.game.Drawable;
import nf.co.mohatram.hidingdot.game.ObjectProperty;

/**
 * Created by madan on 4/22/17.
 */
public abstract class ObjectView implements Drawable, Disposable{
    protected ObjectProperty objectProperty;

    public ObjectView(ObjectProperty objectProperty){
        this.objectProperty=objectProperty;
    }

    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

}
