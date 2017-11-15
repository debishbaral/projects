package nf.co.mohatram.game.model;

/**
 * Created by madan on 4/22/17.
 */
public class LevelFileGenerator {
   /* public static void main(String[] args) throws IOException {
        FileOutputStream fos=new FileOutputStream("level1.json");
        LevelProperty levelProperty=new LevelProperty();

        levelProperty.levelWidth=800f;
        levelProperty.levelHeight=480f;

        levelProperty.breakRow=1;
        levelProperty.breakCol=1;

        levelProperty.playerProperty=new ObjectProperty();
        levelProperty.playerProperty.dimension.set(10f, 10f);
        levelProperty.playerProperty.position.set(100f, 100f);

        levelProperty.getVisibleAreaProperties().addAll(
                new ObjectProperty(200, 200, 30, 30),
                new ObjectProperty(100, 100, 40, 30),
                new ObjectProperty(500, 200, 30, 30),
                new ObjectProperty(195, 256, 30, 40));

        levelProperty.getFriendlyBarriersProperties().addAll(
                new ObjectProperty(100, 100, 3, 100),
                new ObjectProperty(150, 180, 4, 200),
                new ObjectProperty(600, 120, 180, 5),
                new ObjectProperty(700, 20, 300, 4));

        levelProperty.getDeadlyBarriersProperties().addAll(
                new ObjectProperty(3, 10, 3, 80),
                new ObjectProperty(10, 300, 400, 4),
                new ObjectProperty(700, 100, 300, 5),
                new ObjectProperty(70, 200, 3, 4)
        );

        levelProperty.getStarsProperties().addAll(
                new ObjectProperty(100, 100, 30, 30),
                new ObjectProperty(190, 180, 30, 30),
                new ObjectProperty(600, 500, 30, 30));

        levelProperty.finishPointAreaProperty=new ObjectProperty(320, 480, 40, 40);


        Json json=new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        fos.write(json.toJson(levelProperty).getBytes());
    }*/
}
