package nf.co.mohatram.grescrabble;

import org.mads.iotapipub.discovery.ServiceManager;
import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

/**
 * Created by madan on 6/29/17.
 */

public class ServerRegistrationService {

    public static final String key_service_name="service_name";
    public static final String key_name_of_player ="player_name";
    public static final String service_name="gre_service";

    private ServiceManager serviceManager;
    private ServiceInfo serviceInfo;

    public ServerRegistrationService(String playerName){
        serviceManager=ServiceManager.getServiceManager();
        serviceInfo=new ServiceInfo().setDefault();
        serviceInfo.put(key_service_name, service_name);
        serviceInfo.put(key_name_of_player, playerName);
    }

    public void registerService(IRegistrationListener registrationListener){
        serviceManager.registerService(serviceInfo, registrationListener);
    }


    public void unregisterService(IRegistrationListener registrationListener) {
        serviceManager.unregisterService(serviceInfo, registrationListener);
    }
}
