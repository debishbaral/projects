package org.mads.iotapipub.discovery.implementation;

import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madan on 5/17/17.
 */
public class ProviderThread extends Thread {
    public static final int max_packet_size = 2048; //packet cannot exceed more than given bytes
    public static final int time_out_time = 5000;
    private List<ServiceInfoRegListenerPair> serviceInfoList = new ArrayList<>();
    private MulticastSocket multicastSocket;
    private InetAddress listenAddress;
    private boolean running;
    private DatagramPacket toSendDatagramPacket;

    public ProviderThread(InetAddress listenAddress, int listenPort) throws Exception {
        this.listenAddress = listenAddress;
        multicastSocket = new MulticastSocket(listenPort);
        multicastSocket.setSoTimeout(time_out_time);
        toSendDatagramPacket = new DatagramPacket(new byte[max_packet_size], max_packet_size);
        //System.out.println("provider thread created");
        running = true;
    }


    public void add(ServiceInfo serviceInfo, IRegistrationListener iRegistrationListener) {
        serviceInfoList.add(new ServiceInfoRegListenerPair(serviceInfo, iRegistrationListener));
    }

    public void add(ServiceInfoRegListenerPair serviceInfoRegListenerPair) {
        serviceInfoList.add(serviceInfoRegListenerPair);
    }

    public boolean remove(ServiceInfo serviceInfo) {
        int pos = 0;
        for (ServiceInfoRegListenerPair pair : serviceInfoList) {
            if (pair.serviceInfo.equals(serviceInfo)) {
                break;
            }
            pos++;
        }

        try {
            serviceInfoList.remove(pos);
            return true;
        } catch (IndexOutOfBoundsException e) {
            /*System.out.println(e);*/
            return false;
        }
    }

    @Override
    public void run() {
        //System.out.println("Provider thread is running");
        final byte[] bytes = new byte[max_packet_size];
        final DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        while (isRunning()) {
            datagramPacket.setLength(bytes.length);
            NetworkInterface networkInterface = ProviderThread.getNetworkInterfaceAccordingToPriority(NetInterfaces.wlan, NetInterfaces.seth, NetInterfaces.eth, NetInterfaces.rndis, NetInterfaces.usb, NetInterfaces.lo);
            try {
                multicastSocket.setNetworkInterface(networkInterface);
            } catch (SocketException e) {
                //System.out.println(e);
            }

            try {
                multicastSocket.joinGroup(listenAddress);
            } catch (IOException e) {
                //System.out.println(e);
            }

            try {
                multicastSocket.receive(datagramPacket);
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.decode(datagramPacket);

                System.out.println("found from consumer");
                serviceInfo.print();
                notifyClientRequest(serviceInfo);
                notifyServiceDetected(serviceInfo);
            } catch (SocketTimeoutException timeout) {
                //System.out.println(timeout);
            } catch (IOException e) {
                //System.out.println(e);
            } catch (ServiceInfo.UndesirableDatagramException e) {
                //System.out.println(e);
            }
        }
    }

    private void notifyClientRequest(ServiceInfo serviceInfo) {
        for (ServiceInfoRegListenerPair pair : serviceInfoList) {
            if (serviceInfo.isSubsetOf(pair.serviceInfo)) {
                if (pair.iRegistrationListener != null) {
                    pair.iRegistrationListener.onClientRequest(serviceInfo);
                }
            }
        }
    }

    private void notifyServiceDetected(ServiceInfo serviceInfo) {
        for (ServiceInfoRegListenerPair pair : serviceInfoList) {
            if (serviceInfo.isSubsetOf(pair.serviceInfo)) {
                try {
                    pair.serviceInfo.export(toSendDatagramPacket);
                    toSendDatagramPacket.setAddress(serviceInfo.getAddress());
                    toSendDatagramPacket.setPort(serviceInfo.getPort());
                    multicastSocket.send(toSendDatagramPacket);
                    pair.serviceInfo.print();
                } catch (IOException e) {
                    //System.out.println(e);
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean contains(ServiceInfo serviceInfo) {
        for (ServiceInfoRegListenerPair regListenerPair : serviceInfoList) {
            if (regListenerPair.serviceInfo.equals(serviceInfo)) return true;
        }
        return false;
    }

    public void close() {
        multicastSocket.close();
        running = false;
    }

    protected enum NetInterfaces {
        wlan, eth, seth, lo, usb, //teathering features usb
        rndis //while usb teathering

    }

    public static class ServiceInfoRegListenerPair {
        public ServiceInfo serviceInfo;
        public IRegistrationListener iRegistrationListener;

        public ServiceInfoRegListenerPair() {
        }

        public ServiceInfoRegListenerPair(ServiceInfo serviceInfo, IRegistrationListener iRegistrationListener) {
            this.serviceInfo = serviceInfo;
            this.iRegistrationListener = iRegistrationListener;
        }
    }

    public static NetworkInterface getNetworkInterfaceAccordingToPriority(NetInterfaces... priority) {
        List<NetworkInterface> allInterfaces = NetworkInterfaceHelper.getAll();
        NetworkInterface requiredNetworkInterface = null;
        int current = priority.length;

        for (NetworkInterface networkInterface : allInterfaces) {
            System.out.println(networkInterface.getDisplayName());
            for (int i = 0; i < current; i++) {
                if (networkInterface.getDisplayName().contains((priority[i] + "").substring(0, 1))) {
                    if (current > i) {
                        requiredNetworkInterface = networkInterface;
                        current = i;
                    }
                }
            }
        }
        return requiredNetworkInterface;
    }
}

