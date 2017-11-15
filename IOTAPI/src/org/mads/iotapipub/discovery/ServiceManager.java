package org.mads.iotapipub.discovery;

import org.mads.iotapipub.discovery.implementation.ServiceProvider;
import org.mads.iotapipub.discovery.implementation.ServiceConsumer;
import org.mads.iotapipub.discovery.implementation.ImpServiceManager;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

/**
 * Created by madan on 1/15/17.
 */
public abstract class ServiceManager {
    private static ServiceManager serviceManager;
    public static ServiceManager getServiceManager(){
        if (ServiceManager.serviceManager==null){
            ServiceManager.serviceManager=new ImpServiceManager();
        }
        return ServiceManager.serviceManager;
    }
    protected ServiceManager(){}


    public abstract void registerService(ServiceInfo serviceInfo, IRegistrationListener registrationListener);

    public abstract void unregisterService(ServiceInfo serviceInfo, IRegistrationListener registrationListener);

    public abstract void startDiscovery(ServiceInfo serviceInfo, IDiscoveryListener iDiscoveryListener);

    public abstract void stopDiscovery(ServiceInfo serviceInfo, IDiscoveryListener iDiscoveryListener);

}
