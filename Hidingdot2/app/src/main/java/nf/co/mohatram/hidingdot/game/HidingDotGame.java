package nf.co.mohatram.hidingdot.game;

import com.badlogic.gdx.Game;

import nf.co.mohatram.hidingdot.DatabaseHelper;
import nf.co.mohatram.hidingdot.DotApplication;
import nf.co.mohatram.hidingdot.GameActivity;


public class HidingDotGame extends Game {
    public final GameActivity gameActivity;
    public final DatabaseHelper databaseHelper;
    public GameScreen gameScreen;

    public HidingDotGame(GameActivity gameActivity){
        this.gameActivity = gameActivity;
        this.databaseHelper= DotApplication.get().getDatabaseHelper();
    }

    @Override
    public void create() {
        this.gameScreen=new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameScreen.dispose();
    }
}
