package com.example.madan.wifimessaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String TOOL_BAR_NAME = "Wifi messanger";
    public static final int SERVER_PORT = 45678;

    private FloatingActionMenu fabMenu;
    private EditText messageTypeEditTxt;

    private RecyclerView recyclerView;

    private Toolbar toolbar;

    private FragmentManager fragmentManager;
    private MessageAdaptor messageAdaptor;

    private LooperThread looperThread;
    private EditNameFragment editNameFragment;
    private JoinGroupFragment joinGroupFragment;

    protected WmsServer wmsServer;
    protected WmsClient wmsClient;

    protected String groupName = null;


    public static float dpToPixel(Context context, float dp) {
        Resources res = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
        return px;
    }

    public FloatingActionMenu getFabMenu() {
        return fabMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.mainActivity = this;

        initialize();

    }

    public void onSendMessage(View view) {
        if (MainActivityState.wmsClientActive) {
            final String msg = messageTypeEditTxt.getText().toString().trim();
            Log.e("Madan", msg);
            if (msg == null || msg.length() <= 0) {
                return;
            }
            looperThread.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Message mes = new Message(Setting.USER_NAME, msg, Setting.rgb.r, Setting.rgb.g, Setting.rgb.b, Setting.USER_ID);
                    if (wmsClient != null) wmsClient.sendMessage(mes);
                }
            });
            messageTypeEditTxt.setText("");
        } else {
            Toast.makeText(this, "Oops! no connection found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivityState.advertisingServiceActive) startServiceAdvertisement();
        if (MainActivityState.discoveryServiceActive) startDiscoveryService();
    }

    private void startDiscoveryService() {
        Intent intent = new Intent(MainActivity.this, WmsDiscoveryService.class);
        startService(intent);
    }

    private void startServiceAdvertisement() {
        Intent intent = new Intent(MainActivity.this, WmsAdvertisingService.class);
        startService(intent);
    }

    private void closeAdvertisement(){

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Global.wmsAdvertisingService!=null) Global.wmsAdvertisingService.stopSelf();
        if (MainActivityState.discoveryServiceActive) closeDiscoveryService();

    }

    private void closeDiscoveryService() {
        if (Global.wmsDiscoveryService != null) Global.wmsDiscoveryService.stopSelf();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MessageQueue.getMessageComponents().clear();
        if(wmsServer!=null) wmsServer.stop();
        if(wmsClient!=null) wmsClient.stop(0);
        MainActivityState.clean();
        if (looperThread != null) looperThread.getHandler().getLooper().quit();
    }


    public void onFabButtonClicked(View view) {
        fabMenu.close(true);
        switch (view.getId()) {
            case R.id.create_group_fab:
                if (!MainActivityState.advertisingServiceActive) {
                    startServiceAdvertisement();
                } else {
                    closeServiceAdvertisement();
                }
                break;
            case R.id.join_group_fab:
                toolbar.setTitle("Available groups");
                joinGroupFragment = new JoinGroupFragment();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.scale_up, R.anim.scale_down).add(R.id.fragment_container, joinGroupFragment, "Join group fragment").commit();
                MainActivityState.joinGroupFragmentActive = true;
                break;
            case R.id.edit_info_fab:
                toolbar.setTitle("Edit information");
                editNameFragment = new EditNameFragment();
                Bundle b = new Bundle();
                b.putString(Setting.KEY_FOR_USER_NAME, Setting.USER_NAME);
                b.putString("key", Setting.KEY_FOR_USER_NAME);
                b.putInt("RED", Setting.rgb.r);
                b.putInt("GREEN", Setting.rgb.g);
                b.putInt("BLUE", Setting.rgb.b);
                editNameFragment.setArguments(b);
                MainActivityState.editNameFragmentActive = true;
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.scale_up, R.anim.scale_down).add(R.id.fragment_container, editNameFragment, "Edit name fragment").commit();
                break;

        }
        reloadView();
    }

    private void closeServiceAdvertisement() {
        if (Global.wmsAdvertisingService != null) {
            Global.wmsAdvertisingService.stopSelf();
        }
    }

    private void reloadView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainActivityState.editNameFragmentActive || MainActivityState.joinGroupFragmentActive || MainActivityState.wmsClientActive) {
                    closeAndHideFabMenu();
                }
                /*if (MainActivityState.advertisingServiceActive) {
                    FloatingActionButton fabCreateGroupButton = (FloatingActionButton) findViewById(R.id.create_group_fab);
                    fabCreateGroupButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                } else {
                    FloatingActionButton fabCreateGroupButton = (FloatingActionButton) findViewById(R.id.create_group_fab);
                    fabCreateGroupButton.setImageDrawable(getResources().getDrawable(R.drawable.fab_add));
                    ;
                }*/
                if (MainActivityState.editNameFragmentActive) {
                    toolbar.setTitle("Edit name");
                } else if (MainActivityState.joinGroupFragmentActive) {
                    toolbar.setTitle("Available group");
                } else if (MainActivityState.wmsClientActive) {
                    toolbar.setTitle(groupName);
                    Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
                    getSupportActionBar().setHomeAsUpIndicator(drawable);
                }


                if (MainActivityState.isClean()) {
                    initializeViews();
                }
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (MainActivityState.editNameFragmentActive) {
            if (editNameFragment != null)
                editNameFragment.onBackPressed();
            MainActivityState.editNameFragmentActive = false;
        } else if (MainActivityState.joinGroupFragmentActive) {
            if (joinGroupFragment != null)
                joinGroupFragment.onBackPressed();
            MainActivityState.joinGroupFragmentActive = false;
        } else {
            this.finish();
        }
    }

    public void removeFragment(Fragment fragment) {
        loadSetting();
        if (fragment != null) fragmentManager.beginTransaction().remove(fragment).commit();
        reloadView();
    }

    public void closeAndHideFabMenu() {
        getFabMenu().close(false);
        getFabMenu().setVisibility(View.INVISIBLE);
    }

    public void showFabMenu() {
        getFabMenu().setVisibility(View.VISIBLE);
        getFabMenu().showMenu(true);
    }

    public void saveSharedPref(String... keyAndValuesPair) {
        int i = 0;
        SharedPreferences sharedPreferences = getSharedPreferences(Setting.SHARED_PERF_NAME, MODE_PRIVATE);
        while (i < keyAndValuesPair.length) {
            try {
                sharedPreferences.edit().putString(keyAndValuesPair[i], keyAndValuesPair[i + 1]).commit();
                Log.e("Check", keyAndValuesPair[i] + ", " + keyAndValuesPair[i + 1]);
            } catch (NullPointerException e) {
                break;
            }
            i = i + 2;
        }
    }

    public void loadSetting() {
        SharedPreferences sharedPreferences = getSharedPreferences(Setting.SHARED_PERF_NAME, MODE_PRIVATE);
        Setting.USER_NAME = sharedPreferences.getString(Setting.KEY_FOR_USER_NAME, "Communicator");

        Setting.rgb.r = Integer.parseInt(sharedPreferences.getString("RED", "-1"));
        Setting.rgb.g = Integer.parseInt(sharedPreferences.getString("GREEN", "-1"));
        Setting.rgb.b = Integer.parseInt(sharedPreferences.getString("BLUE", "-1"));
    }

    public void updateMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdaptor.notifyDataSetChanged();
                recyclerView.invalidate();
                recyclerView.scrollToPosition(messageAdaptor.getItemCount() - 1);
            }
        });
    }


    public LooperThread getLooperThread() {
        return looperThread;
    }


    public void onServerError(Exception e) {

    }


    public void onServiceResolved(final NsdServiceInfo nsdServiceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(getClass().getName(), "service resolved");
                JoinGroupFragment.onServiceResolved(nsdServiceInfo);
                if (MainActivityState.joinGroupFragmentActive) {
                    if (joinGroupFragment != null) {
                        joinGroupFragment.updateGroupList();
                    }
                }
            }
        });
    }

    public void onStartDiscoveryFailed(String s, int i) {
        Log.e(getClass().getName(), "service start discovery failed");
        Toast.makeText(this, "Oops! Try again", Toast.LENGTH_SHORT).show();
        MainActivityState.advertisingServiceActive = false;
        reloadView();
    }

    public void onStopDiscoveryFailed(String s, int i) {
        Log.e(getClass().getName(), "service stop discovery failed");
        MainActivityState.advertisingServiceActive = true;
    }

    public void onDiscoveryStarted(String s) {
        Log.e(getClass().getName(), "service discovery started");
        MainActivityState.advertisingServiceActive = true;
        reloadView();
    }

    public void onDiscoveryStopped(String s) {
        Log.e(getClass().getName(), "service discoverty stopped");
        MainActivityState.advertisingServiceActive = false;
    }

    public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
        Log.e(getClass().getName(), "service lost");
        JoinGroupFragment.onServiceLost(nsdServiceInfo);
        if (MainActivityState.joinGroupFragmentActive) {
            if (joinGroupFragment != null) {
                joinGroupFragment.updateGroupList();
            }
        }
    }

    public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
        Log.e(getClass().getName(), "service reg failed");
    }

    public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
        Log.e(getClass().getName(), "service unreg failed");
    }

    public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
        Log.e(getClass().getName(), "service registered");
    }

    public void onServiceUnRegistered(NsdServiceInfo nsdServiceInfo) {
        Log.e(getClass().getName(), "service unreg");
    }

    public void clientIsClosed(int reason) {
        Toast.makeText(this, "Oops! Unable to reach server.", Toast.LENGTH_LONG).show();
    }

    public void onClientError(Exception e) {
        Toast.makeText(this, "Oops! Somehow connection is failed", Toast.LENGTH_SHORT).show();
        Log.e("Erroe", "error", e);
        MainActivityState.wmsClientActive = false;
        if (wmsClient != null) wmsClient.stop(0);
    }

    public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {

    }


    public static class MainActivityState {
        public static boolean editNameFragmentActive = false;
        public static boolean joinGroupFragmentActive = false;

        public static boolean discoveryServiceActive = false;
        public static boolean advertisingServiceActive = false;

        public static boolean wmsServerActive = false;
        public static boolean wmsClientActive = false;
        public static boolean wmsClientRunning = false;

        public static void clean() {
            editNameFragmentActive = false;
            joinGroupFragmentActive = false;
            discoveryServiceActive = false;
            advertisingServiceActive = false;
            wmsServerActive = false;
            wmsClientRunning = false;
            wmsClientActive = false;
        }

        public static boolean isClean() {
            if (editNameFragmentActive == false &&
                    joinGroupFragmentActive == false &&
                    discoveryServiceActive == false &&
                    advertisingServiceActive == false &&
                    wmsServerActive == false &&
                    wmsClientRunning == false &&
                    wmsClientActive == false) {
                return true;
            }
            return false;
        }
    }

    protected void joinGroup(final String address, final int serverPort, final Context context, final String groupName) {
        getLooperThread().getHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.this.wmsClient = new WmsClient(address, serverPort, context);
                    MainActivity.MainActivityState.wmsClientActive = true;
                } catch (IOException e) {
                    MainActivity.this.onClientError(e);
                }
                if (wmsClient != null) wmsClient.setRunning(true);
                MainActivity.this.groupName = groupName;
                new Thread(MainActivity.this.wmsClient).start();
                MainActivityState.wmsClientRunning = true;

                reloadView();
            }
        });
    }

    void initialize() {
        initializeViews();
        Setting.USER_ID = new Random().nextInt(99999);
        loadSetting();


        if (looperThread == null) {
            looperThread = new LooperThread();
            looperThread.start();
        }

        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
    }

    private void initializeViews() {
        if (fabMenu == null) fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fabMenu.setVisibility(View.VISIBLE);
        messageTypeEditTxt = (EditText) findViewById(R.id.message_type_et);
        messageTypeEditTxt.clearFocus();

        if (messageAdaptor == null) {
            messageAdaptor = new MessageAdaptor(this, MessageQueue.getMessageComponents());
        }

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.message_area_view);
            recyclerView.setAdapter(messageAdaptor);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        ((MessageAdaptor) recyclerView.getAdapter()).messageComponents.clear();
        messageAdaptor.notifyDataSetChanged();
        recyclerView.invalidate();

        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        setSupportActionBar(toolbar);
        if(MainActivityState.wmsClientActive)toolbar.setTitle(groupName);
        else toolbar.setTitle(TOOL_BAR_NAME);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
}
