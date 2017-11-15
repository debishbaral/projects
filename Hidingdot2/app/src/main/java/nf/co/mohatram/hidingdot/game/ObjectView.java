package nf.co.mohatram.hidingdot.game;


/**
 * Created by madan on 4/22/17.
 */
public abstract class ObjectView implements Drawable{
    public ObjectProperty objectProperty;

    public ObjectView(ObjectProperty objectProperty){
        this.objectProperty=objectProperty;
    }

    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    public abstract int getType();

}
