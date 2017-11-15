package nf.co.mohatram.hidingdot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Handler handler;
    private FirebaseRemoteConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        databaseHelper=DotApplication.get().getDatabaseHelper();
        FirebaseApp.initializeApp(this);
        remoteConfig = FirebaseRemoteConfig.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initFireBaseConfig();
        intiSettings();

        handler.postDelayed(openMainActivityRunnable, 2000);
    }

    private Runnable openMainActivityRunnable=new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
    private void intiSettings() {
        Setting.isAdsActive=databaseHelper.isAdActive();
        Setting.noOfHintsOn1Star=databaseHelper.getHintsOn1Star();
        Setting.noOfHintsOn2Star=databaseHelper.getHintsOn2Star();
        Setting.noOfHintsOn3Star=databaseHelper.getHintsOn3Star();
        Setting.noOfHintsOnFBShare=databaseHelper.getHintsOnFBShare();
        Setting.timeGapBetweenShares=databaseHelper.getGapBetweenEachShare();

       /* Setting.log();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfFromNotification();
    }
    private void checkIfFromNotification() {
        Bundle data = getIntent().getExtras();
        if (data == null) {
            //Log.e("Intent", "Intent is nul;");
            return;
        }
        try {
            //Log.e("Data", data.toString() + data.get("ads") + data.get("link"));
            if (data.getString("ads").contains("tr")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getString("link")));
                startActivity(intent);
                this.finish();
            }
        } catch (Exception e) {

        }
        data.clear();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(openMainActivityRunnable);
        finish();
    }


    private void initFireBaseConfig(){
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build());

        Map<String, Object> defaults=new HashMap<>();
        defaults.put(DatabaseHelper.key_is_ads_active, databaseHelper.isAdActive());
        defaults.put(DatabaseHelper.key_no_of_hints_1_star, databaseHelper.getHintsOn1Star());
        defaults.put(DatabaseHelper.key_no_of_hints_2_star, databaseHelper.getHintsOn2Star());
        defaults.put(DatabaseHelper.key_no_of_hints_3_star, databaseHelper.getHintsOn3Star());
        defaults.put(DatabaseHelper.key_no_of_hints_on_fb_share, databaseHelper.getHintsOnFBShare());
        defaults.put(DatabaseHelper.key_gap_between_each_share, databaseHelper.getGapBetweenEachShare());

        remoteConfig.setDefaults(defaults);

        Task<Void> fetch = remoteConfig.fetch();
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                updateFirebaseConfig();
            }
        });

    }

    public void updateFirebaseConfig(){
        databaseHelper.setAdsActive(remoteConfig.getBoolean(DatabaseHelper.key_is_ads_active));
        databaseHelper.setNoOfHints1Star(Integer.parseInt(remoteConfig.getString(DatabaseHelper.key_no_of_hints_1_star)));
        databaseHelper.setNoOfHints2Star(Integer.parseInt(remoteConfig.getString(DatabaseHelper.key_no_of_hints_2_star)));
        databaseHelper.setNoOfHints3Star(Integer.parseInt(remoteConfig.getString(DatabaseHelper.key_no_of_hints_3_star)));
        databaseHelper.setNoOfHintsOnFBShare(Integer.parseInt(remoteConfig.getString(DatabaseHelper.key_no_of_hints_on_fb_share)));
        databaseHelper.setGapBetweenEachShare(Integer.parseInt(remoteConfig.getString(DatabaseHelper.key_gap_between_each_share)));
        /*Log.e("Remote config", remoteConfig.toString());*/
    }

}
