package org.mads.iotapipub.discovery.implementation;

import org.mads.iotapipub.discovery.listeners.ErrorCode;
import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceProvider only advertiser services with
 */
public class ServiceProvider {
    private static Map<ServiceInfo.SocketDetails, ProviderThread> serviceInfoThreadMap = new HashMap<>();

    private ServiceProvider() {
    }

    protected static void registerService(final ServiceInfo serviceInfo, final IRegistrationListener iRegistrationListener) {
        for (ServiceInfo.SocketDetails sd : serviceInfoThreadMap.keySet()) {
            /*System.out.println(sd.address+":"+sd.port);*/
            if (sd.equals(serviceInfo.getSocketDetails())) {
                //System.out.println("Provider has already thread for socket details");
                ProviderThread providerThread = serviceInfoThreadMap.get(sd);
                if (providerThread.contains(serviceInfo)) {
                    System.out.println("Provider contains service info");
                    if (iRegistrationListener != null) {
                        iRegistrationListener.onServiceRegistrationFailed(serviceInfo, ErrorCode.FatalError.SERVICE_ALREADY_REGISTERED, null);
                    }
                    return;
                } else {
                    providerThread.add(new ProviderThread.ServiceInfoRegListenerPair(serviceInfo, iRegistrationListener));
                    if (iRegistrationListener!=null){
                        iRegistrationListener.onServiceRegistrationSuccess(serviceInfo);
                    }
                }
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProviderThread providerThread = new ProviderThread(serviceInfo.getAddress(), serviceInfo.getPort());
                    serviceInfoThreadMap.put(serviceInfo.getSocketDetails(), providerThread);
            /*System.out.println("Put into the map");*/
                    providerThread.add(serviceInfo, iRegistrationListener);
                    providerThread.start();
                    if (iRegistrationListener != null) {
                        iRegistrationListener.onServiceRegistrationSuccess(serviceInfo);
                    }
                } catch (Exception e) {
            /*System.out.println("Hello failed");
            System.out.println(e);
            e.printStackTrace();*/
                    if (iRegistrationListener != null) {
                        iRegistrationListener.onServiceRegistrationFailed(serviceInfo, ErrorCode.FatalError.UNABLE_TO_CREATE_SOCKET_CONNECTION, e);
                    }
                }
            }
        }).start();

    }

    public static void unregisterService(ServiceInfo serviceInfo, IRegistrationListener iRegistrationListener){

        for (ServiceInfo.SocketDetails sd : serviceInfoThreadMap.keySet()) {
            if (sd.equals(serviceInfo.getSocketDetails())) {
                ProviderThread providerThread = serviceInfoThreadMap.get(sd);
                boolean remove = providerThread.remove(serviceInfo);
                if (remove){
                    if (iRegistrationListener!=null) {
                        iRegistrationListener.onServiceUnregistrationSuccess(serviceInfo);
                    }
                }else{
                    if (iRegistrationListener!=null){
                        iRegistrationListener.onServiceUnregistrationFailed(serviceInfo, ErrorCode.NonFatalError.SERVICE_NOT_REGISTERED, null);
                    }
                }
                return;
            }
        }
        if (iRegistrationListener!=null){
            iRegistrationListener.onServiceUnregistrationFailed(serviceInfo, ErrorCode.NonFatalError.SERVICE_NOT_REGISTERED, null);
        }
    }
}