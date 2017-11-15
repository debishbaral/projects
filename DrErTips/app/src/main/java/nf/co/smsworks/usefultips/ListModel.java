package nf.co.smsworks.usefultips;

import android.content.Intent;

/**
 * Created by madan on 6/23/17.
 */

public class ListModel {
    public int id;
    public String fileName;
    public String title;
    public String desc;
    public String image;

    public void encodeTointent(Intent intent){
        intent.putExtra(key_id, id);
        intent.putExtra(key_title, title);
        intent.putExtra(key_filename, fileName);
        intent.putExtra(key_des, desc);
        intent.putExtra(key_image, image);
    }

    public void decodeFromIntent(Intent intent){
        id=intent.getIntExtra(key_id, -1);
        title=intent.getStringExtra(key_title);
        desc=intent.getStringExtra(key_des);
        fileName=intent.getStringExtra(key_filename);
        image=intent.getStringExtra(key_image);
    }

    public static final String key_id="id";
    public static final String key_title="title";
    public static final String key_filename="filename";
    public static final String key_des="desc";
    public static final String key_image="image";
}
