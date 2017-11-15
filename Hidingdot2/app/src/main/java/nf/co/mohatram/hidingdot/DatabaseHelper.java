package nf.co.mohatram.hidingdot;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * Created by madan on 5/3/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String database_name = "hiding_dot.db";
    public static final String level_map_table = "level_map_table";
    public static final String col_level_file_name = "level_file";
    public static final String col_available_since = "available_since";
    public static final String col_id = "id";
    public static final String col_no_of_stars = "no_of_stars";
    public static final String col_locked = "locked";

    public static final String key_music_on = "cfed124a1f";
    public static final String key_sound_on = "ef34beafdc";
    public static final String key_no_of_hints = "1afde234ab";
    public static final String key_no_of_hint_hash = "198dfeac2b";
    public static final String key_first_play = "82f09ac9c1";


    public static final String level_map_location = "levelmap.map";
    public static final String shared_preference_name = "preferences.shared";
    public static final int initial_hints = 20;

    private Context context;

    public DatabaseHelper(Context context, int version) {
        super(context, database_name, null, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String qCreateTableLevelMap = "CREATE TABLE " + level_map_table + "(" +
                "" + col_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + col_level_file_name + " TEXT NOT NULL UNIQUE," +
                "" + col_available_since + " INT NOT NULL," +
                "" + col_no_of_stars + " INT NOT NULL DEFAULT 0, " +
                "" + col_locked + " INT NOT NULL DEFAULT 1);";

        db.execSQL(qCreateTableLevelMap);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(level_map_location)));
            String mLine;

            while ((mLine = bufferedReader.readLine()) != null) {
                if (mLine.startsWith("#")) {
                    String[] split = mLine.split(",");
                    String levelName = split[0].substring(1);
                    int availableSince = Integer.parseInt(split[1].trim());
                    insertIntoLevelMap(db, levelName, availableSince);
                }
            }

            db.execSQL("UPDATE " + level_map_table + " SET " + col_locked + "=" + 0 + " WHERE " + col_id + "=" + 1+ ";");//set level 1 unlocked at start
        } catch (IOException e) {throw new RuntimeException(level_map_location + " file doesn't exists.");}
        initializeSettings();
    }

    private void initializeSettings() {
        setMusicOn(false);
        setSoundOn(false);
        setNoOfHints(initial_hints);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(level_map_location)));
            String mLine;

            while ((mLine = bufferedReader.readLine()) != null) {
                if (mLine.startsWith("#")) {
                    String[] split = mLine.split(",");
                    String levelName = split[0].substring(1);
                    int availableSince = Integer.parseInt(split[1].trim());
                    if (availableSince > oldVersion) {
                        insertIntoLevelMap(db, levelName, availableSince);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(level_map_location + " file doesn't exists.");
        }
    }

    private boolean insertIntoLevelMap(SQLiteDatabase db, String filename, int avaliableSince) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_level_file_name, filename);
        contentValues.put(col_available_since, avaliableSince);
        long insert = db.insert(level_map_table, null, contentValues);
        return insert == -1 ? false : true;
    }

    public boolean isMusicOn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key_music_on, false);
    }

    public void setMusicOn(boolean b) {
        Log.e("Unwanted execution", "Music set to "+b);
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key_music_on, b);
        editor.commit();
    }

    public int getNoOfHints() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        int noOfHints = sharedPreferences.getInt(key_no_of_hints, 0);
        String hash = sharedPreferences.getString(key_no_of_hint_hash, "");
        if (!hash.contains(hash(noOfHints + ""))) {
            setNoOfHints(initial_hints);
            return getNoOfHints();
        }
        return noOfHints;
    }

    public synchronized void setNoOfHints(int noOfHints) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_no_of_hints, noOfHints);
        editor.putString(key_no_of_hint_hash, hash(noOfHints + ""));
        editor.commit();
    }

    public synchronized void addNoOfHints(int n){
        setNoOfHints(getNoOfHints()+n);
    }
    public int update(int id, ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(level_map_table, contentValues, "" + col_id + "=" + id, null);
    }

    public void getLevelSelectorModels(List<LevelModel> levelModels) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = getAllFromLevelMap(readableDatabase);
        cursor.moveToFirst();

        int index=0;
        do {
            LevelModel levelModel = new LevelModel();
            levelModel.levelId = cursor.getInt(cursor.getColumnIndex(col_id));
            /*Log.e("check", ""+levelModel.levelId);*/
            levelModel.levelLocked = cursor.getInt(cursor.getColumnIndex(col_locked)) == 1 ? true : false;
            levelModel.levelFileName = cursor.getString(cursor.getColumnIndex(col_level_file_name));
            levelModel.noOfStars = cursor.getInt(cursor.getColumnIndex(col_no_of_stars));
            levelModel.levelNo=index;
            index++;
            levelModels.add(levelModel);
        } while (cursor.moveToNext());

        cursor.close();
    }

    private Cursor getAllFromLevelMap(SQLiteDatabase sqLiteDatabase) {
        return sqLiteDatabase.rawQuery("SELECT * FROM " + level_map_table + ";", null);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(level_map_table, col_available_since + "> ?", new String[]{newVersion + ""});
    }



    public boolean isSoundOn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key_sound_on, false);
    }

    public void setSoundOn(boolean soundOn) {
        //Log.e("Unwanted execution", "Sound set to "+soundOn);
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key_sound_on, soundOn);
        editor.commit();
    }

    public boolean isFirstTimePlayed() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key_first_play, true);
    }

    public synchronized void setNoFirstTime() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key_first_play, false);
        editor.commit();
    }




    public long getLastSharedTime(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key_last_shared_time, 0);
    }

    public  synchronized void setLastSharedTime(long time){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key_last_shared_time, time);
        editor.commit();
    }

    public static String hash(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(text.getBytes());
            return new String(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            /*Log.e("NoSuchAlgorithm", "Algorithm error", e);*/
            return null;
        }
    }

    public int getHintsOn1Star(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key_no_of_hints_1_star, 4);
    }

    public int getHintsOn2Star(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key_no_of_hints_2_star, 6);
    }

    public int getHintsOn3Star(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key_no_of_hints_3_star, 8);
    }

    public synchronized void setNoOfHints1Star(int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_no_of_hints_1_star, n);
        editor.commit();
    }
    public synchronized void setNoOfHints2Star(int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_no_of_hints_2_star, n);
        editor.commit();
    }
    public synchronized void setNoOfHints3Star(int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_no_of_hints_3_star, n);
        editor.commit();
    }

    public int getHintsOnFBShare(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key_no_of_hints_on_fb_share, 20);
    }

    public synchronized void setNoOfHintsOnFBShare(int n){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_no_of_hints_on_fb_share, n);
        editor.commit();
    }

    public synchronized void setGapBetweenEachShare(int time){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_gap_between_each_share, time);
        editor.commit();
    }

    public int getGapBetweenEachShare(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key_gap_between_each_share, 3*60*60*1000); //3 hrs
    }

    public boolean isAdActive(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key_is_ads_active, true); //of course yes
    }

    public synchronized void setAdsActive(boolean active){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key_is_ads_active, active);
        editor.commit();
    }

    public synchronized void setAlreadyRated(boolean b){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key_already_rated, b);
        editor.commit();
    }

    public boolean isAlreadyRated(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(shared_preference_name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key_already_rated, false);
    }

    public static final String key_is_ads_active="ads_active";
    public static final String key_last_shared_time ="last_shared_time";
    public static final String key_no_of_hints_1_star="no_of_hints_on_1_star";
    public static final String key_no_of_hints_2_star="no_of_hints_on_2_star";
    public static final String key_no_of_hints_3_star="no_of_hints_on_3_star";
    public static final String key_no_of_hints_on_fb_share="no_of_hints_on_fb_share";
    public static final String key_gap_between_each_share="gap_between_each_share";
    public static final String key_already_rated="already_rated";

}
