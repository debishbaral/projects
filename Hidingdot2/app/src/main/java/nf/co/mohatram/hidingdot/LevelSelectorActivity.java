package nf.co.mohatram.hidingdot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import nf.co.mohatram.hidingdot.game.EndingScreen;

public class LevelSelectorActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 123;
    public static final int FACEBOOK_REQUEST_CODE = 366;
    public static final int movement_threshold = 0;
    public static final int hiding_time_fam = 2000;

    private boolean paused;

    private boolean floatingMenuActive = true;
    private boolean recyclerViewScrolledNotHandled = false;

    private DatabaseHelper databaseHelper;
    private FacebookCallback<Sharer.Result> shareCallback;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private ShareLinkContent shareContent;
    private InterstitialAd interstitialAd;
    private AdRequestThread adRequestThread;
    private Runnable adRequestRunnable;

    private RecyclerView recyclerView;
    private FloatingActionMenu floatingActionMenu;
    private ImageView ivCurrentAverageStars;
    private ShowLevelTextView showLevelTextView;
    private TextView tvNoOfHints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level_selector);

        recyclerView = (RecyclerView) findViewById(R.id.rv_level_selector_grids);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam_share);
        ivCurrentAverageStars = (ImageView) findViewById(R.id.iv_current_average_rating);
        showLevelTextView = (ShowLevelTextView) findViewById(R.id.stv_current_level);
        tvNoOfHints = (TextView) findViewById(R.id.tv_no_of_hints_available);

        initViews();
        initFacebook();

        initAds();
    }

    private void initAds() {
        interstitialAd = new InterstitialAd(this);
        adRequestThread = new AdRequestThread();
        adRequestThread.start();


        adRequestRunnable = new Runnable() {
            @Override
            public void run() {

                if (Setting.isAdsActive) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestNewAds();
                        }
                    });
                }
            }
        };

        interstitialAd.setAdUnitId(getString(R.string.full_screen_ads_release));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (Setting.isAdsActive) requestNewAds();
            }


            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adRequestThread.handler.postDelayed(adRequestRunnable, 5000);
            }
        });

        if (Setting.isAdsActive) requestNewAds();
    }

    private void requestNewAds() {
        //Log.e("Ads", " requesting new ads");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device))
                .build();
        interstitialAd.loadAd(adRequest);
        adRequestThread.handler.removeCallbacks(adRequestRunnable);
    }

    private void showAds() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(this);
        shareCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(LevelSelectorActivity.this, "Thanks for sharing. " + Setting.noOfHintsOnFBShare + " more hints added.", Toast.LENGTH_LONG).show();
                databaseHelper.setLastSharedTime(System.currentTimeMillis());
                databaseHelper.addNoOfHints(Setting.noOfHintsOnFBShare);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LevelSelectorActivity.this, "Somehow share was unsuccessful. Sorry.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LevelSelectorActivity.this, "There seems some problems in sharing. Sorry", Toast.LENGTH_LONG).show();
            }
        };


        shareDialog = new ShareDialog(this);

        callbackManager = new CallbackManager.Factory().create();
        shareDialog.registerCallback(callbackManager, shareCallback, LevelSelectorActivity.FACEBOOK_REQUEST_CODE);
        shareContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(getString(R.string.app_play_url)))
                .build();
    }

    public void onclickFaceBookShareBtn(View view) {
        /*long currentTimeMillis = System.currentTimeMillis();
        long lastSharedTime = databaseHelper.getLastSharedTime();
        int diffn = (int) (currentTimeMillis - lastSharedTime);
        if (diffn > Setting.timeGapBetweenShares) {*/
            shareDialog.show(shareContent);
        /*} else {
            int rem = (Setting.timeGapBetweenShares - diffn) / 1000;

            int hour = rem / 3600;
            int min = (rem % 3600) / 60;
            int sec = (rem % 3600) % 60;
            Toast.makeText(this, "You have to wait fo additional " + hour + "H:" + min + "M:" + sec + "S before sharing.", Toast.LENGTH_LONG).show();
        }*/
    }

    private void initViews() {
        databaseHelper = DotApplication.get().getDatabaseHelper();
        LevelSelectorAdaptor adapter = new LevelSelectorAdaptor(this);
        databaseHelper.getLevelSelectorModels(adapter.levelModels);
        final Animation animation = AnimationUtils.loadAnimation(LevelSelectorActivity.this, R.anim.hide_fam);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                floatingActionMenu.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerViewScrolledNotHandled = true;
                if (Math.abs(dy) > movement_threshold) {
                    if (floatingMenuActive) {
                        floatingActionMenu.close(true);
                        floatingMenuActive = false;
                        floatingActionMenu.startAnimation(animation);

                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (recyclerViewScrolledNotHandled && !paused) {
                                        recyclerViewScrolledNotHandled = false;
                                        Thread.sleep(hiding_time_fam);
                                    }
                                    LevelSelectorActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            floatingMenuActive = true;
                                            floatingActionMenu.setVisibility(View.VISIBLE);
                                            Animation animation1 = AnimationUtils.loadAnimation(LevelSelectorActivity.this, R.anim.show_fam);
                                            floatingActionMenu.startAnimation(animation1);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                }
                            }
                        });
                        thread.start();
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        refreshViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LevelSelectorActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int currentLevel = data.getIntExtra(EndingScreen.key_this_level, -1);
                boolean levelWon = data.getBooleanExtra(EndingScreen.key_level_won, false);
                int noOfStarsWon = data.getIntExtra(EndingScreen.key_no_of_stars, 0);
                if (currentLevel == -1) return;
                LevelModel currentLevl = ((LevelSelectorAdaptor) recyclerView.getAdapter()).getItemWithId(currentLevel);
                if (levelWon) {
                    if (noOfStarsWon > currentLevl.noOfStars) {
                        currentLevl.noOfStars = noOfStarsWon;
                    }
                }

                recyclerView.getAdapter().notifyItemChanged(currentLevl.levelNo);
                try {
                    LevelModel nextLevel = ((LevelSelectorAdaptor) recyclerView.getAdapter()).getItem(currentLevl.levelNo + 1);
                    if (levelWon) {
                        if (nextLevel.levelLocked) {
                            nextLevel.levelLocked = false;
                        }
                    }
                    recyclerView.getAdapter().notifyItemChanged(nextLevel.levelNo);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        } else if (requestCode == LevelSelectorActivity.FACEBOOK_REQUEST_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshViews() {
        showLevelTextView.setLevel(getCurrentLevel(((LevelSelectorAdaptor) recyclerView.getAdapter()).levelModels));
        int averageRating = (int) ((float) getTotalNoOfStars(((LevelSelectorAdaptor) recyclerView.getAdapter()).levelModels) / (float) getNoOfUnlockedLevels(((LevelSelectorAdaptor) recyclerView.getAdapter()).levelModels) + 0.5f);
        if (averageRating == 1) {
            ivCurrentAverageStars.setImageResource(R.drawable.star1);
        } else if (averageRating == 2) {
            ivCurrentAverageStars.setImageResource(R.drawable.star2);
        } else if (averageRating == 3) {
            ivCurrentAverageStars.setImageResource(R.drawable.star3);
        } else {
            ivCurrentAverageStars.setImageResource(R.drawable.star0);
        }
        tvNoOfHints.setText(databaseHelper.getNoOfHints() + "");
    }

    private int getCurrentLevel(List<LevelModel> levelModels) {
        int cl = 0;
        for (int i = 0; i < levelModels.size(); i++) {
            if (levelModels.get(i).levelLocked) {
                break;
            }
            cl = i;
        }
        return cl + 1;
    }

    private int getNoOfUnlockedLevels(List<LevelModel> levelModels) {
        int n = 0;
        for (LevelModel lvm : levelModels) {
            if (!lvm.levelLocked) n++;
        }
        return n;
    }

    private int getTotalNoOfStars(List<LevelModel> levelModels) {
        int s = 0;
        for (LevelModel lm : levelModels) {
            s += lm.noOfStars;
        }
        return s;
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_frm_right, R.anim.anim_slide_to_left_exit);
        paused = false;
        refreshViews();
        showAds();

        if (MusicService.get() != null) MusicService.get().resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MusicService.get() != null) MusicService.get().pauseMusic();
    }
}
