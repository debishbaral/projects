package org.mads.iotapipub.discovery.listeners;

import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.List;

/**
 * Created by madan on 1/16/17.
 * These callbacks may be called outside the thread where they are initialized.
 * In such case the context of the thread may be lost.
 */
public interface IDiscoveryListener {

    /**
     * Executed when there is problem on starting discovery
     * @param errorCode
     * @param serviceInfo
     */
    void onStartDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e);

    /**
     * Executed when there is problem on stopping discovery
     * @param errorCode
     * @param serviceInfo
     */
    void onStopDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e);

    /**
     *
     */
    void onStartDiscoverySuccess(ServiceInfo serviceInfo);
    void onStopDiscoverySuccess(ServiceInfo serviceInfo);

    void onDiscoverAttempt(ServiceInfo serviceInfo, int i);

    /**
     * Executed whenever any service which is the superset of registered service is found
     * @param newServiceInfo new services that is found
     * @param alreadyFoundList services that are found
     */
    void onServiceFound(List<ServiceInfo> alreadyFoundList, ServiceInfo newServiceInfo);

    void onCorruptedServiceFound(ServiceInfo serviceInfo, Exception e);

    void onDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e);
}
