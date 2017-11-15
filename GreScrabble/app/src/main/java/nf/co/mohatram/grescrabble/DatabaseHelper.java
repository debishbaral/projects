package nf.co.mohatram.grescrabble;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by madan on 8/8/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String database_name = "gre_question.db";
    public static final String level_map_table = "level_map_table";
    public static final String col_question_file = "question_file";
    public static final String col_category = "question_category";
    public static final String col_id = "id";
    public static final String file_map = "file_map.mapp";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, database_name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qCreateTableLevelMap = "CREATE TABLE " + level_map_table + "(" +
                "" + col_id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + col_category + " INTEGER NOT NULL," +
                "" + col_question_file + " TEXT NOT NULL);";
        db.execSQL(qCreateTableLevelMap);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(file_map)));
            String mLine;

            while ((mLine = bufferedReader.readLine()) != null) {
                if (mLine.startsWith("#")) {
                    String levelName = mLine.substring(1);
                    Log.e("LevelName", levelName);
                    insertIntoTable(db, levelName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(file_map + " file doesn't exists.");
        }
    }

    private void insertIntoTable(SQLiteDatabase sqLiteDatabase, String levelName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_question_file, levelName);
        int category;
        if (levelName.contains("ana")) {
            category = QuestionType.analogy;
        } else if (levelName.contains("wor")) {
            category = QuestionType.word_game;
        } else if (levelName.contains("ant")) {
            category = QuestionType.antonyms;
        } else if (levelName.contains("sent")) {
            category = QuestionType.sentence_completion;
        } else {
            category = QuestionType.unknown;
        }

        contentValues.put(col_category, category);
        sqLiteDatabase.insert(level_map_table, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getQuestionForType(int questionType){
        String query="SELECT * FROM "+level_map_table+" WHERE "+col_category +" LIKE "+questionType;
        return getReadableDatabase().rawQuery(query, null);
    }

    public static class QuestionType {
        public static final int analogy = 0;
        public static final int word_game = 10;
        public static final int antonyms = 100;
        public static final int sentence_completion = 1000;
        public static final int unknown = -10;
    }

    public static class TimeForQuestions {
        public static final long analogy = 20000;
        public static final long word_game = 5000;
        public static final long antonyms = 5000;
        public static final long sentence_completion = 30000;
        public static final long unknown = 0;
    }

    public static long getTimeForQuestionType(int questionType) {
        switch (questionType) {
            case QuestionType.analogy:
                return TimeForQuestions.analogy;
            case QuestionType.antonyms:
                return TimeForQuestions.antonyms;
            case QuestionType.sentence_completion:
                return TimeForQuestions.sentence_completion;
            case QuestionType.word_game:
                return TimeForQuestions.word_game;
            default:
                return TimeForQuestions.unknown;
        }
    }

}
