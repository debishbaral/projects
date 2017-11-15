package nf.co.mohatram.grescrabble;

import org.mads.iotapipub.discovery.ServiceManager;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

/**
 * Created by madan on 6/29/17.
 */

public class ClientDiscoveryService {
    private ServiceManager serviceManager;
    private ServiceInfo serviceInfo;
    private String nameOfPlayer;

    public ClientDiscoveryService(String nameOfPlayer){
        this.nameOfPlayer = nameOfPlayer;
        serviceManager=ServiceManager.getServiceManager();
        serviceInfo=new ServiceInfo().setDefault();
        serviceInfo.put(ServerRegistrationService.key_service_name, ServerRegistrationService.service_name);
    }

    public void startDiscovery(IDiscoveryListener iDiscoveryListener){
        serviceManager.startDiscovery(serviceInfo, iDiscoveryListener);
    }

    public void stopDiscovery(IDiscoveryListener iDiscoveryListener){
        serviceManager.stopDiscovery(serviceInfo, iDiscoveryListener);
    }
}
