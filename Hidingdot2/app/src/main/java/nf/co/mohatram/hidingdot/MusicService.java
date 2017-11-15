package nf.co.mohatram.hidingdot;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnErrorListener {
    /*private static boolean music_playing = false;
    private static boolean sound_playing = true;*/
    private static boolean cheer_on=false;

    private static  MusicService musicService;

    /*private final IBinder iBinder = new ServiceBinder();*/
    private MediaPlayer mpBackground;
    private int length = new Random().nextInt(50000);
    private MediaPlayer mpCrowdCheer;
    private MediaPlayer mpDeadSound;
    private MediaPlayer mpBtnClick;
    private  boolean soundPlaying;
    private  boolean musicPlaying;

    public MusicService() {
    }

    public static MusicService get() {
        return musicService;
    }

    public  void setSoundPlaying(boolean soundPlaying) {
        this.soundPlaying = soundPlaying;
    }

    public  void setMusicPlaying(boolean musicPlaying) {
        this.musicPlaying = musicPlaying;
    }

    /*public static boolean wasMusicPlaying() {
        return music_playing;
    }*/

    /*public static void setMusicPlaying(boolean was) {
        music_playing = was;
    }*/


    /*public static void setSoundPlaying(boolean sound_playing) {
        MusicService.sound_playing = sound_playing;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        musicService=this;

        handler = new Handler();
        AssetFileDescriptor afd;
        mpBackground = new MediaPlayer();
        mpCrowdCheer = new MediaPlayer();
        mpDeadSound =new MediaPlayer();
        mpBtnClick=new MediaPlayer();

        mpDeadSound.setOnErrorListener(this);
        mpBackground.setOnErrorListener(this);
        mpCrowdCheer.setOnErrorListener(this);
        try {
            afd = getAssets().openFd("sounds/puzzle_game.mp3");
            mpBackground.setLooping(true);
            mpBackground.setVolume(1f, 1f);
            mpBackground.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mpBackground.prepare();

            afd = getAssets().openFd("sounds/crowdcheering.mp3");
            mpCrowdCheer.setLooping(false);
            mpCrowdCheer.setVolume(1f, 1f);
            mpCrowdCheer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mpCrowdCheer.prepare();

            afd = getAssets().openFd("sounds/dead.mp3");
            mpDeadSound.setLooping(false);
            mpDeadSound.setVolume(.80f, .80f);
            mpDeadSound.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mpDeadSound.prepare();


            afd=getAssets().openFd("sounds/btnclick.mp3");
            mpBtnClick.setLooping(false);
            mpBtnClick.setVolume(1f, 1f);
            mpBtnClick.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mpBtnClick.prepare();
        } catch (IOException e) {
            /*Log.e("ERROR ASSETS", "error", e);*/
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void pauseMusic() {
        if (mpBackground != null) {
            if (mpBackground.isPlaying()) {
                mpBackground.pause();
                length = mpBackground.getCurrentPosition();
            }
        }
        if (mpCrowdCheer !=null){
            if (mpCrowdCheer.isPlaying()) mpCrowdCheer.pause();
        }
    }

    public void resumeMusic() {
        if (musicPlaying ) {
            if (mpBackground!=null && !mpBackground.isPlaying()) {
                mpBackground.seekTo(length);
                mpBackground.start();
            }else {
                pauseMusic();
                if (mpBackground!=null)resumeMusic();
            }
            if (cheer_on){
                cheerOnWin();
            }
        }else {
            pauseMusic();
        }
    }

   /* public void stopMusic() {
        if (mpBackground != null) {
            mpBackground.stop();
            mpBackground.release();
            mpBackground = null;
        }
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        musicService=null;
        if (mpBackground != null) {

            try {
                mpBackground.stop();
                mpBackground.release();
            } finally {
                mpBackground = null;
            }
        }
        if (mpCrowdCheer !=null){
            try{
                mpCrowdCheer.stop();
                mpCrowdCheer.release();
            }finally {
                mpCrowdCheer =null;
            }
        }

        if (mpDeadSound !=null){
            try{
                mpDeadSound.stop();
                mpDeadSound.release();
            }finally {
                mpDeadSound =null;
            }
        }
        if (mpBtnClick!=null){
            try {
                mpBtnClick.stop();
                mpBtnClick.release();
            }finally {
                mpBtnClick=null;
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "UNABLE TO START MUSIC SERVICE", Toast.LENGTH_LONG).show();
        musicPlaying=false;
        soundPlaying=false;
        if (mpBackground != null) {
            try {
                mpBackground.stop();
                mpBackground.release();
            } finally {
                mpBackground = null;
            }
        }
        return false;
    }

    private Runnable cheerStopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mpCrowdCheer.isPlaying()){
                mpCrowdCheer.pause();
            }
            mpCrowdCheer.setVolume(1, 1);
        }
    };



    public void cheerOnStar() {
        if (soundPlaying ) {
            if(!mpCrowdCheer.isPlaying()) {
                /*Log.e("What is the error man", "Errorrrr r");*/
                mpCrowdCheer.setVolume(0.30f, 0.30f);
                mpCrowdCheer.seekTo(4000);
                mpCrowdCheer.start();
                handler.postDelayed(cheerStopRunnable, 1200);
            }else {
                mpCrowdCheer.pause();
                cheerOnStar();
            }
        }
    }

    public void cheerOnWin(){
        cheer_on=true;
        if (musicPlaying ){
            if (!mpCrowdCheer.isPlaying()){
                mpCrowdCheer.setVolume(0.30f, 0.30f);
                mpCrowdCheer.setLooping(false);
                mpCrowdCheer.seekTo(0);
                mpCrowdCheer.start();
            }/*else {
                mpCrowdCheer.pause();
                cheerOnWin();
            }*/
        }
    }

    public void stopCheerOnWin(){
        cheer_on=false;
        if (mpCrowdCheer.isPlaying()){
            mpCrowdCheer.pause();
        }
    }

    public void onDeadSoundEffect(){
        if (soundPlaying) {
            if (!mpDeadSound.isPlaying()) {
                mpDeadSound.seekTo(0);
                mpDeadSound.start();
            }/*else {
                mpDeadSound.pause();
                onDeadSoundEffect();
            }*/
        }
    }


    /*public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }*/

    public void playOnBtnClick(){
        if (soundPlaying) {
            if (!mpBtnClick.isPlaying()) {
                mpBtnClick.setLooping(false);
                mpBtnClick.seekTo(0);
                mpBtnClick.start();
            }else {
                mpBtnClick.pause();
                playOnBtnClick();
            }
        }
    }
}
