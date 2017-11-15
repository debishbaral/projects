package org.mads.iotapipub.discovery.implementation;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by madan on 1/27/17.
 */

public class NetworkInterfaceHelper {
    public static List<NetworkInterface> getInterfaces(String name) {
        List<NetworkInterface> networkInterfaceList = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getDisplayName().contains(name)) {
                    networkInterfaceList.add(networkInterface);
                }
            }
        } catch (Exception e) {}

        return networkInterfaceList;
    }

    public static List<NetworkInterface> getWirelessInterfaces() {
        return NetworkInterfaceHelper.getInterfaces("wlan");
    }

    public static List<NetworkInterface> getEthernetInterfaces(){
        return NetworkInterfaceHelper.getInterfaces("eth");
    }

    public static List<NetworkInterface> getSethInterfaces(){
        return NetworkInterfaceHelper.getInterfaces("seth");
    }

    public static List<NetworkInterface> getAll(){
        return NetworkInterfaceHelper.getInterfaces("");
    }
}
