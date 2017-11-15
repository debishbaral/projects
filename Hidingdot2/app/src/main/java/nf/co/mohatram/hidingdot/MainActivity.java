package nf.co.mohatram.hidingdot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import nf.co.mohatram.hidingdot.tutorial.TutorialActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private CircleButton musicToggleBtn;
    private CircleButton soundToggleBtn;
    private CircleButton rateUsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        musicToggleBtn = (CircleButton) findViewById(R.id.music_toggle);
        soundToggleBtn= (CircleButton) findViewById(R.id.sound_toggle);
        rateUsBtn=(CircleButton) findViewById(R.id.rate_us_btn);

        databaseHelper = DotApplication.get().getDatabaseHelper();
        MusicService.get().setMusicPlaying(databaseHelper.isMusicOn());
        MusicService.get().setSoundPlaying(databaseHelper.isSoundOn());
        if (databaseHelper.isAlreadyRated()) rateUsBtn.setVisibility(View.GONE);
    }

    public void onClickMusicToggleBtn(View view) {
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        boolean previouslyMusicOn = databaseHelper.isMusicOn();
        databaseHelper.setMusicOn(!previouslyMusicOn);
        MusicService.get().setMusicPlaying(!previouslyMusicOn);
        if(previouslyMusicOn){
            MusicService.get().pauseMusic();
        }else {
            MusicService.get().resumeMusic();
        }
        refreshUI();
    }

    public void onClickRateUsBtn(View view) {
        /*Log.e("check____", "rate us btn clicked");*/
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
            }

            @Override
            public void onNoClicked() {
                databaseHelper.setAlreadyRated(true);
                ((RotatingCircularButton)rateUsBtn).setAnimating(false);
                rateUsBtn.setVisibility(View.INVISIBLE);
                rateUsBtn.setVisibility(View.GONE);
            }

            @Override
            public void onCancelClicked() {

            }
        });
        RateThisApp.showRateDialog(this);
    }

    public void onClickedInfoButton(View view) {
        /*Log.e("check____", "info btn clicked");*/
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();

        AboutUsFragment aboutUsFragment=new AboutUsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_activity_pl, aboutUsFragment).commit();
    }

    public void onPlayButtonClicked(View view) {
        if (playClicked) return;
        playClicked=true;
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();

        Intent i;
        i= new Intent(MainActivity.this, LevelSelectorActivity.class);
        /*if(databaseHelper.isFirstTimePlayed()) {
            i = new Intent(MainActivity.this, TutorialActivity.class);
        }else {
            i= new Intent(MainActivity.this, LevelSelectorActivity.class);
        }*/
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        startActivity(i);
    }

    public void onClickTutorialBtn(View view) {
        /*Log.e("check____", "tutorial btn clicked");*/
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        Intent intent=new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_frm_right, R.anim.anim_slide_to_left_exit);
        refreshUI();
        if (MusicService.get() != null) MusicService.get().resumeMusic();
    }

    private void refreshUI() {
        if (databaseHelper.isMusicOn()) {
            musicToggleBtn.setImageResource(R.drawable.music_on);
            Log.e("unwanted execution", "Why music is on");
        } else {
            musicToggleBtn.setImageResource(R.drawable.music_off);
            Log.e("unwanted execution", "Why music is off");
        }

        if (databaseHelper.isSoundOn()) {
            soundToggleBtn.setImageResource(R.drawable.sound_btn_on);
        }else{
            soundToggleBtn.setImageResource(R.drawable.sound_btn_off);
        }

    }




    public void onClickSoundToggleBtn(View view) {
        boolean soundOn=databaseHelper.isSoundOn();
        databaseHelper.setSoundOn(!soundOn);
        MusicService.get().setSoundPlaying(!soundOn);
        if (MusicService.get()!=null)MusicService.get().playOnBtnClick();
        refreshUI();
    }

    private boolean playClicked=false;
    @Override
    protected void onPause() {
        super.onPause();
        playClicked=false;
        if (MusicService.get() != null) MusicService.get().pauseMusic();
    }
}
