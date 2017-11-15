package testing;

import org.mads.iotapipub.discovery.ServiceManager;
import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

/**
 * Created by madan on 5/17/17.
 */
public class Advertisement {
    public static void main(String[] args) {
        ServiceManager serviceManager=ServiceManager.getServiceManager();
        IRegistrationListener iRegistrationListener=new IRegistrationListener() {
            @Override
            public void onServiceRegistrationSuccess(ServiceInfo serviceInfo) {
                System.out.println("registration successful");
            }

            @Override
            public void onServiceRegistrationFailed(ServiceInfo serviceInfo, int errorCode, Exception e) {
                System.out.println("Registration failed "+errorCode);
            }

            @Override
            public void onServiceUnregistrationSuccess(ServiceInfo serviceInfo) {
                System.out.println("Unregistration unsuccessful");
            }

            @Override
            public void onServiceUnregistrationFailed(ServiceInfo serviceInfo, int errorcode, Exception e) {
                System.out.println("Unregistration failed");
            }

            @Override
            public void onClientRequest(ServiceInfo serviceInfo) {
                System.out.println("Client requested for this.");
            }
        };

        ServiceInfo serviceInfo=new ServiceInfo().setDefault();
        serviceInfo.put("service_name", "madan");
        serviceInfo.put("code", "java");
        serviceManager.registerService(serviceInfo, iRegistrationListener);
        ServiceInfo serviceInfo2=new ServiceInfo().setDefault();
        serviceInfo2.put("service_name", "madan");
        serviceInfo2.put("code", "java");
        serviceInfo2.put("hurry", "hcodeXXXX");
        serviceManager.registerService(serviceInfo2, iRegistrationListener);

    }
}
