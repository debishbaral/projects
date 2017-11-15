package testing;

import org.mads.iotapipub.discovery.ServiceManager;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.List;


public class Discovery {
    public static void main(String[] args) {
        ServiceManager serviceManager = ServiceManager.getServiceManager();
        ServiceInfo serviceInfoS = new ServiceInfo();
        IDiscoveryListener iDiscoveryListener = new IDiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e) {
                System.out.println("Discovery start failed.");
            }

            @Override
            public void onStopDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e) {
                System.out.println("Discovery stop failed."+errorCode+e);
            }

            @Override
            public void onStartDiscoverySuccess(ServiceInfo serviceInfo) {
                System.out.println("Discovery start success.");
            }

            @Override
            public void onStopDiscoverySuccess(ServiceInfo serviceInfo) {
                System.out.println("Discovery stop success.");
            }

            @Override
            public void onDiscoverAttempt(ServiceInfo serviceInfo, int i) {
                //System.out.println("Trying to discover " + i);
            }

            @Override
            public void onServiceFound(List<ServiceInfo> infos,  ServiceInfo serviceInfo) {
                System.out.println("Service info found");
                serviceInfo.print();
                //serviceManager.stopDiscovery(serviceInfoS, this);
            }

            @Override
            public void onCorruptedServiceFound(ServiceInfo serviceInfo, Exception e) {
                e.printStackTrace();
                serviceInfo.print();
            }

            @Override
            public void onDiscoveryFailed(ServiceInfo serviceInfo, int errorCode, Exception e) {
                System.out.println("Discovery failed"+ errorCode);
                serviceInfo.print();
            }
        };
        serviceInfoS.setDefault();
        serviceInfoS.put("service_name", "madan");

        serviceManager.startDiscovery(serviceInfoS, iDiscoveryListener);

    }
}
