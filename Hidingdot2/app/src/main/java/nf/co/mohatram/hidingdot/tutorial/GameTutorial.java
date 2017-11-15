package nf.co.mohatram.hidingdot.tutorial;

import com.badlogic.gdx.Game;

/**
 * Created by madan on 5/9/17.
 */

public class GameTutorial extends Game {

    public final TutorialActivity tutorialActivity;

    public GameTutorial(TutorialActivity tutorialActivity) {
        this.tutorialActivity = tutorialActivity;
    }

    @Override
    public void create() {
        setScreen(new TutorialScreen(this));
    }
}
