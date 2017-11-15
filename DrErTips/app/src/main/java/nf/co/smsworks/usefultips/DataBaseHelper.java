package nf.co.smsworks.usefultips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by madan on 6/23/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final int database_version = 1;
    public static final String database_name = "tips_database.db";
    public static final String table_tips_map = "table_tips_map";
    public static final String col_filename = "filename";
    public static final String col_id = "id";
    public static final String col_name = "name";
    public static final String col_desc = "desc";
    public static final String col_feature_image = "feature_image";
    public static final String col_available_since = "available_since";
    public static final String map_file_name = "contents.map";

    private Context context;

    public DataBaseHelper(Context context) {
        super(context, database_name, null, database_version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a table
        String qCreateTable = "CREATE TABLE " + table_tips_map + "(" +
                "" + col_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "" + col_filename + " TEXT NOT NULL, " +
                "" + col_name + " TEXT NOT NULL, " +
                "" + col_desc + " TEXT, " +
                "" + col_feature_image + " TEXT, " +
                "" + col_available_since+ " INT NOT NULL DEFAULT "+database_version+
                ");";
        Log.e("Query", qCreateTable);
        db.execSQL(qCreateTable);

        insertIntoDatabase(db);
        Log.e("Successful", "Successfully parsed");
    }

    private void insertIntoDatabase(SQLiteDatabase db) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(map_file_name)));
            String mLine;

            while ((mLine = bufferedReader.readLine()) != null) {
                if (mLine.startsWith("#")) {
                    String[] split = mLine.split(",");
                    String contentFile = split[0].substring(1);
                    int availableSince = Integer.parseInt(split[1].trim());

                    addToDatabase(db, contentFile, availableSince);
                }
            }
        } catch (IOException e) {
            logError(e);
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            logError(e);
            e.printStackTrace();
        } catch (SAXException e) {
            logError(e);
            e.printStackTrace();
        }
    }

    private void addToDatabase(SQLiteDatabase db, String contentFile, int availableSince) throws IOException, ParserConfigurationException, SAXException {
        //parse content file
        InputStream inputStream = context.getAssets().open(contentFile);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        Element documentElement = document.getDocumentElement();
        String tableName = table_tips_map;
        String fileName = contentFile;
        String tipName = documentElement.getAttribute("name");
        String tipsDesc = documentElement.getAttribute("desc");
        String fImage = documentElement.getAttribute("image");
        if (!insertIntoTable(db, tableName, fileName, tipName, tipsDesc, fImage, availableSince)) {
            throw new RuntimeException("Error in parsing");
        }
        Log.e("Contentsfound", fileName + " " + tipName + " " + tipsDesc + " " + fImage + " " + availableSince);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void logError(Exception e) {
        Log.e("DAtabaseHelper", e.toString(), e);
    }

    private boolean insertIntoTable(SQLiteDatabase db, String tableName, String contentFile, String tipsName, String tipsDesc, String featureImage, int availableSince) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_filename, contentFile);
        contentValues.put(col_name, tipsName);
        contentValues.put(col_desc, tipsDesc);
        contentValues.put(col_feature_image, featureImage);
        contentValues.put(col_available_since, availableSince);
        long insert = db.insert(tableName, null, contentValues);
        return insert == -1 ? false : true;
    }

    public void getTipsModel(List<ListModel> listModels){
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM " + table_tips_map, null);
        if (cursor.getCount()<=0) return;

        cursor.moveToFirst();
        do{
            ListModel listModel=new ListModel();
            listModel.id=cursor.getInt(cursor.getColumnIndex(col_id));
            listModel.title=cursor.getString(cursor.getColumnIndex(col_name));
            listModel.desc=cursor.getString(cursor.getColumnIndex(col_desc));
            listModel.image=cursor.getString(cursor.getColumnIndex(col_feature_image));
            listModel.fileName=cursor.getString(cursor.getColumnIndex(col_filename));
            listModels.add(listModel);
        }while (cursor.moveToNext());
    }
}
