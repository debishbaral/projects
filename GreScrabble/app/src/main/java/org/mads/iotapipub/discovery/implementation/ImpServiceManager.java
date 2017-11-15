package org.mads.iotapipub.discovery.implementation;

import org.mads.iotapipub.discovery.ServiceManager;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

/**
 * Created by madan on 1/21/17.
 */
public class ImpServiceManager extends ServiceManager {

    @Override
    public void registerService(ServiceInfo serviceInfo, IRegistrationListener registrationListener) {
        ServiceProvider.registerService(serviceInfo, registrationListener);
    }

    @Override
    public void unregisterService(ServiceInfo serviceInfo, IRegistrationListener registrationListener) {
        ServiceProvider.unregisterService(serviceInfo, registrationListener);
    }

    @Override
    public void startDiscovery(ServiceInfo serviceInfo, IDiscoveryListener iDiscoveryListener) {
        ServiceConsumer.startDiscovery(serviceInfo, iDiscoveryListener);
    }

    @Override
    public void stopDiscovery(ServiceInfo serviceInfo, IDiscoveryListener iDiscoveryListener) {
        ServiceConsumer.stopDiscovery(serviceInfo, iDiscoveryListener);
    }
}
