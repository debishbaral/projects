package nf.co.smsworks.usefultips;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TipsViewHolder.OnItemClickListener {

    private RecyclerView recyclerView;
    private DataBaseHelper dataBaseHelper;
    private TipsAdaptor tipsAdaptor;
    private List<ListModel> listModelLis;


    private AboutUsFragment aboutUsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBaseHelper = DrErTipsApplication.get().getDataBaseHelper();
        listModelLis = new ArrayList<>();
        dataBaseHelper.getTipsModel(listModelLis);

        initViews();
        initAds();
        aboutUsFragment = new AboutUsFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("Checking", "Checking if from notification");
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
            Log.e("ExceptionUKK", "", e);
        }
        data.clear();
    }

    private AdView adView;

    private void initAds() {
        adView = (AdView) findViewById(R.id.ads_view);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                requestAds();
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });
        requestAds();
    }

    private void requestAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("3429977F293870C2512D7C359F8FB4D1")
                .build();

        adView.loadAd(adRequest);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.tips_list_rv_ma);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        tipsAdaptor = new TipsAdaptor(this, listModelLis);
        recyclerView.setAdapter(tipsAdaptor);
        ActionBar toolbar = getSupportActionBar();
        //toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        toolbar.setIcon(R.drawable.ic_home);
    }

    @Override
    public void onItemClicked(View v, int position) {
        Intent intent = new Intent(this, InnerActivity.class);
        ListModel listModel = tipsAdaptor.getItem(position);
        listModel.encodeTointent(intent);
        this.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean fragmentActive = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about: {
                if (!fragmentActive)
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, aboutUsFragment).commit();
                fragmentActive = true;
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!fragmentActive) super.onBackPressed();
        else {
            aboutUsFragment.hideFragment();
            fragmentActive = false;
        }
    }
}
