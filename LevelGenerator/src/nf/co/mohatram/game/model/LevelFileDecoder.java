package nf.co.mohatram.game.model;

import com.badlogic.gdx.utils.Json;
import nf.co.mohatram.hidingdot.game.LevelProperty;

import java.io.FileInputStream;

/**
 * Created by madan on 4/26/17.
 */
public class LevelFileDecoder {
    public static Json json=new Json();
    public static LevelProperty decode(String filename){
        try {
            FileInputStream inputStream=new FileInputStream(filename);
            StringBuilder builder=new StringBuilder();
            int read;
            while (true){
                read=inputStream.read();
                if (read==-1 && ((char)read)!='\n' &&((char)read)!='\r') break;

                builder.append((char)read);
            }

            return json.fromJson(LevelProperty.class, builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
