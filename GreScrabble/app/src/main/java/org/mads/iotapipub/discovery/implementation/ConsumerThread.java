package org.mads.iotapipub.discovery.implementation;

import org.mads.iotapipub.discovery.listeners.ErrorCode;
import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madan on 5/17/17.
 */
public class ConsumerThread extends Thread {
    public boolean found;
    ;
    private IDiscoveryListener iDiscoveryListener;
    private ServiceInfo serviceInfo;
    private boolean running;
    private MulticastSocket multicastSocket;
    private DatagramPacket sendPacket;
    private byte[] sendBytes;
    private byte[] receiveBytes;
    private ServiceInfo serviceInfoReceived;
    private List<ServiceInfo> alreadyFound = new ArrayList<>();
    private DatagramPacket receivePacket;
    private MulticastSocket receiveMulticastSocket;
    private ListenThread listenThread;

    public ConsumerThread(IDiscoveryListener iDiscoveryListener, ServiceInfo serviceInfo) throws IOException {
        this.iDiscoveryListener = iDiscoveryListener;
        this.serviceInfo = serviceInfo;
        running = true;
        multicastSocket = new MulticastSocket();
        sendBytes = new byte[ProviderThread.max_packet_size];
        receiveBytes = new byte[ProviderThread.max_packet_size];
        sendPacket = new DatagramPacket(sendBytes, 0, sendBytes.length);
        receivePacket = new DatagramPacket(receiveBytes, 0, receiveBytes.length);
        multicastSocket.setSoTimeout(ProviderThread.time_out_time);
        serviceInfoReceived = new ServiceInfo();
        found = false;

        receiveMulticastSocket = new MulticastSocket(multicastSocket.getLocalPort());
        receiveMulticastSocket.setSoTimeout(ProviderThread.time_out_time);

        listenThread = new ListenThread();
        new Thread(listenThread).start();
    }


    @Override
    public void run() {
        int n = 0;
        while (isRunning()) {
            n++;
            if (iDiscoveryListener != null) {
                iDiscoveryListener.onDiscoverAttempt(serviceInfo, n);
            }
            NetworkInterface networkInterface = ProviderThread.getNetworkInterfaceAccordingToPriority(ProviderThread.NetInterfaces.wlan, ProviderThread.NetInterfaces.seth, ProviderThread.NetInterfaces.eth, ProviderThread.NetInterfaces.rndis, ProviderThread.NetInterfaces.usb, ProviderThread.NetInterfaces.lo);

            serviceInfo.export(sendPacket);
            try {
                multicastSocket.setNetworkInterface(networkInterface);
            } catch (SocketException e) {/*//e.printStackTrace();*/}

            try {
                multicastSocket.send(sendPacket);

                //send to the router
                if (!networkInterface.isLoopback()) {
                    for (InterfaceAddress ifa : networkInterface.getInterfaceAddresses()) {
                        byte[] address = ifa.getAddress().getAddress();
                        if (address.length == 4) {
                            //it is ipv4 address and use it
                            int addressInt = byteArrayToInt(address);

                            int subnetCIDR = ifa.getNetworkPrefixLength();
                            int subnetMask = getClassFulSubnetMask(subnetCIDR);

                            int routerAddress = getRouterAddress(addressInt, subnetMask);

                            sendPacket.setAddress(InetAddress.getByAddress(intToByteArray(routerAddress)));
                            multicastSocket.send(sendPacket);
                        }
                    }
                }
            } catch (IOException e) {
                /*e.printStackTrace();*/
                //try loopback
                sendPacket.setAddress(InetAddress.getLoopbackAddress());
                try {
                    multicastSocket.send(sendPacket);
                } catch (IOException el) {
                    /*e1.printStackTrace();*/
                    if (iDiscoveryListener != null) {
                        iDiscoveryListener.onDiscoveryFailed(serviceInfo, ErrorCode.FatalError.UNABLE_TO_CONNECT_THROUGH_SOCKET, el);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAlreadyFound(ServiceInfo serviceInfoReceived) {
        for (ServiceInfo si :
                alreadyFound) {
            if (si.equals(serviceInfoReceived)) return true;
        }
        return false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void close() {
        multicastSocket.close();
        listenThread.running = false;
        receiveMulticastSocket.close();
        setRunning(false);
    }

    private static int byteArrayToInt(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        return wrapped.getInt();
    }

    private static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(i).array();
    }

    public static int getClassFulSubnetMask(int cidr) {
        if (cidr > 32 || cidr < 0)
            throw new IllegalArgumentException("Invalid cidr range of " + cidr + "\n should be in range 0-32");
        int bitMask0 = 0xffffffff;
        return bitMask0 << (32 - cidr);
    }

    public static int getRouterAddress(int yourIp, int subnetMask) {
        return (yourIp & subnetMask) + 1;
    }

    public class ListenThread implements Runnable {

        public boolean running = true;

        @Override
        public void run() {
            System.out.println("Running listen thread");
            /*System.out.println(receiveMulticastSocket.getLocalPort()+receiveMulticastSocket.getInetAddress().getHostAddress());
            System.out.println(multicastSocket.getLocalPort()+multicastSocket.getInetAddress().getHostAddress());*/
            while (running) {
                NetworkInterface networkInterface = ProviderThread.getNetworkInterfaceAccordingToPriority(ProviderThread.NetInterfaces.wlan, ProviderThread.NetInterfaces.seth, ProviderThread.NetInterfaces.eth, ProviderThread.NetInterfaces.rndis, ProviderThread.NetInterfaces.usb/*, ProviderThread.NetInterfaces.lo*/);
                try {
                    receiveMulticastSocket.setNetworkInterface(networkInterface);
                    System.out.println("Network interface connection done"+networkInterface.getName());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    receivePacket.setData(receiveBytes);
                    receivePacket.setLength(receiveBytes.length);
                    receiveMulticastSocket.receive(receivePacket);
                    serviceInfoReceived.decode(receivePacket);
                    if (!isAlreadyFound(serviceInfoReceived)) {
                        alreadyFound.add(new ServiceInfo().copy(serviceInfoReceived));
                        if (iDiscoveryListener != null) {
                            iDiscoveryListener.onServiceFound(alreadyFound, serviceInfoReceived);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                } catch (ServiceInfo.UndesirableDatagramException e) {
                    e.printStackTrace();
                    if (iDiscoveryListener != null) iDiscoveryListener.onCorruptedServiceFound(serviceInfoReceived, e);
                }
            }
        }
    }
}
