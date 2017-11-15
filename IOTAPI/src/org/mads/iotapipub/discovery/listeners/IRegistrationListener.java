package org.mads.iotapipub.discovery.listeners;

import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;
/*
* These callbacks may be called outside the thread where they are initialized.
* In such case the context of the thread may be lost.
*/

public interface IRegistrationListener {

    void onServiceRegistrationSuccess(ServiceInfo serviceInfo);

    void onServiceRegistrationFailed(ServiceInfo serviceInfo, int errorCode, Exception e);

    void onServiceUnregistrationSuccess(ServiceInfo serviceInfo);

    void onServiceUnregistrationFailed(ServiceInfo serviceInfo, int errorcode, Exception e);

    void onClientRequest(ServiceInfo serviceInfo);
}
