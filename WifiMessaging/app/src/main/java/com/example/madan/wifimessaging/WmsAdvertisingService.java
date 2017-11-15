package com.example.madan.wifimessaging;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;

import java.io.IOException;
import java.util.Random;

public class WmsAdvertisingService extends Service {

    public static final String SERVICE_CONTENT = "wms_server_manager";
    public static final String SERVICE_TYPE = "_http._tcp";
    private String serverName = Setting.USER_NAME;

    private NsdManager nsdManager;
    private NsdServiceInfo nsdServiceInfo;
    private NsdManager.RegistrationListener registrationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Global.wmsAdvertisingService = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Global.mainActivity != null) {
                    try {
                        Global.mainActivity.wmsServer = new WmsServer(MainActivity.SERVER_PORT);
                        Global.mainActivity.wmsServer.setRunning(true);
                        new Thread(Global.mainActivity.wmsServer).start();

                        if(Global.mainActivity!=null) Global.mainActivity.joinGroup("127.0.0.1", MainActivity.SERVER_PORT, WmsAdvertisingService.this, Setting.USER_NAME);
                    } catch (IOException e) {
                        Global.mainActivity.onServerError(e);
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setServiceType(SERVICE_TYPE);
        nsdServiceInfo.setServiceName(serverName + SERVICE_CONTENT + new Random().nextInt(999999));
        nsdServiceInfo.setPort(34477);

        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                if (Global.mainActivity != null)
                    Global.mainActivity.onRegistrationFailed(nsdServiceInfo, i);
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                if (Global.mainActivity != null)
                    Global.mainActivity.onUnregistrationFailed(nsdServiceInfo, i);
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                if (Global.mainActivity != null)
                    Global.mainActivity.onServiceRegistered(nsdServiceInfo);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                if (Global.mainActivity != null)
                    Global.mainActivity.onServiceUnRegistered(nsdServiceInfo);
            }
        };

        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Global.mainActivity!=null && Global.mainActivity.wmsServer!=null){
            Global.mainActivity.wmsServer.stop();
        }
        if (registrationListener != null) nsdManager.unregisterService(registrationListener);
        Global.wmsAdvertisingService = null;
    }
}
