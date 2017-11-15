package nf.co.smsworks.usefultips;

import android.app.Application;

/**
 * Created by madan on 6/23/17.
 */

public class DrErTipsApplication extends Application{
    private static DrErTipsApplication drErTipsApplication;

    public static DrErTipsApplication get() {
        return drErTipsApplication;
    }


    private DataBaseHelper dataBaseHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        drErTipsApplication=this;
        initDatabase();
    }

    private void initDatabase() {
        dataBaseHelper=new DataBaseHelper(this);
        dataBaseHelper.getWritableDatabase();
    }

    public DataBaseHelper getDataBaseHelper() {
        return dataBaseHelper;
    }
}
