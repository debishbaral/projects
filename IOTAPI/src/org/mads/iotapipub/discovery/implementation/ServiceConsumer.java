package org.mads.iotapipub.discovery.implementation;

import org.mads.iotapipub.discovery.listeners.ErrorCode;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceConsumer {
    private static Map<ServiceInfo, ConsumerThread> infoThreadMap = new HashMap<>();

    public static void startDiscovery(final ServiceInfo serviceInfo, final IDiscoveryListener iDiscoveryListener) {
        for (ServiceInfo sInfo : infoThreadMap.keySet()) {
            if (sInfo.equals(serviceInfo)) {
                if (iDiscoveryListener != null)
                    iDiscoveryListener.onStartDiscoveryFailed(serviceInfo, ErrorCode.FatalError.SERVICE_ALREADY_REGISTERED, null);
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConsumerThread consumerThread = new ConsumerThread(iDiscoveryListener, serviceInfo);
                    infoThreadMap.put(serviceInfo, consumerThread);
                    //System.out.println("Service info added to map");
                    consumerThread.start();
                    if (iDiscoveryListener != null) {
                        iDiscoveryListener.onStartDiscoverySuccess(serviceInfo);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    if (iDiscoveryListener != null) {
                        iDiscoveryListener.onStartDiscoveryFailed(serviceInfo, ErrorCode.FatalError.UNABLE_TO_CREATE_SOCKET_CONNECTION, e);
                    }
                }
            }
        }).start();
    }

    public static void stopDiscovery(ServiceInfo serviceInfo, IDiscoveryListener iDiscoveryListener) {
        ServiceInfo key = null;
        for (ServiceInfo si : infoThreadMap.keySet()) {
            if (si.equals(serviceInfo)) {
                System.out.println("One is found");
                ConsumerThread consumerThread = infoThreadMap.get(si);
                consumerThread.close();
                key = si;
                break;
            }
        }
        Object remove = ((HashMap) infoThreadMap).remove(key);
        if (remove == null) {
            if (iDiscoveryListener != null)
                iDiscoveryListener.onStopDiscoveryFailed(serviceInfo, ErrorCode.NonFatalError.SERVICE_NOT_FOUND, null);
        } else {
            if (iDiscoveryListener != null) iDiscoveryListener.onStopDiscoverySuccess(serviceInfo);
        }

    }


}
