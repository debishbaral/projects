package com.example.madan.wifimessaging;

import android.app.Service;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;

/**
 * Created by madan on 1/5/17.
 */

public class WmsDiscoveryService extends Service {

    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Global.wmsDiscoveryService =this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        nsdManager= (NsdManager) getSystemService(NSD_SERVICE);

        resolveListener=new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                if(Global.mainActivity!=null) Global.mainActivity.onResolveFailed(nsdServiceInfo, i);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                if (Global.mainActivity!=null) Global.mainActivity.onServiceResolved(nsdServiceInfo);
            }
        };

        discoveryListener=new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                ;if (Global.mainActivity!=null) Global.mainActivity.onStartDiscoveryFailed(s, i);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                if (Global.mainActivity!=null) Global.mainActivity.onStopDiscoveryFailed(s, i);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                if (Global.mainActivity!=null) Global.mainActivity.onDiscoveryStarted(s);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                if (Global.mainActivity!=null) Global.mainActivity.onDiscoveryStopped(s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceName().contains(WmsAdvertisingService.SERVICE_CONTENT)) {
                    try {
                        nsdManager.resolveService(serviceInfo, resolveListener);
                    }catch (IllegalArgumentException e){}
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                if (Global.mainActivity!=null) Global.mainActivity.onServiceLost(nsdServiceInfo);
            }
        };

        nsdManager.discoverServices(WmsAdvertisingService.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nsdManager.stopServiceDiscovery(discoveryListener);
        Global.wmsDiscoveryService =null;
    }
}
