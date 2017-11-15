package org.mads.iotapipub.discovery.serviceinfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ServiceInfo implements Serializable{
    public static final byte[] default_discovery_address =new byte[]{(byte) 225, 78, 78, 78};
    public static final int default_discovery_port = 34867;


    private Map<String, String> arguments;
    private InetAddress address; //address used to send the packet
    private int port; //address used to send the packet

    public ServiceInfo() {
        arguments = new HashMap<>();
        socketDetails = new SocketDetails();
    }

    public InetAddress getAddress() {
        return address;
    }

    public ServiceInfo setAddress(InetAddress inetAddress) {
        this.address = inetAddress;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServiceInfo setPort(int port) {
        if (port < 1 || port > 0xff) {
            throw new IllegalArgumentException("Network port addresses should be in range 1-65535");
        }
        this.port = port;
        return this;
    }

    public String put(String key, String value) {
        return arguments.put(key, value);
    }

    public String remove(String key) {
        return arguments.remove(key);
    }

    public String get(String key) {
        return arguments.get(key);
    }

    public Map<String, String> getAll() {
        return arguments;
    }

    public void export(DatagramPacket datagramPacket) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            datagramPacket.setAddress(address);
            datagramPacket.setPort(port);
            objectOutputStream.writeObject(arguments);
            objectOutputStream.flush();
            datagramPacket.setData(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void decode(DatagramPacket datagramPacket) throws UndesirableDatagramException {
        address=datagramPacket.getAddress();
        port=datagramPacket.getPort();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(datagramPacket.getData());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            arguments = (Map<String, String>) objectInputStream.readObject();
        } catch (Exception e) {
            throw new UndesirableDatagramException(e);
        }

    }

    public boolean isSubsetOf(ServiceInfo serviceInfo) {
        for (String key : arguments.keySet()) {
            if (!get(key).equals(serviceInfo.get(key))) return false;
        }
        return true;
    }

    public boolean equals(ServiceInfo serviceInfo) {
        if (this.isSubsetOf(serviceInfo) && serviceInfo.isSubsetOf(this) && port == serviceInfo.port && (address == null || address.equals(serviceInfo.address))) {
            return true;
        }
        return false;
    }

    public void print() {
        System.out.println("address: " + ((address==null)?"":address.getHostAddress()));
        System.out.println("port: " + port);
        for (String key : arguments.keySet()) {
            System.out.println(key + ": " + get(key));
        }
    }

    public SocketDetails getSocketDetails() {
        socketDetails.address=address.getHostAddress();
        socketDetails.port=port;
        return socketDetails;
    }

    public ServiceInfo setDefault(){
        try {
            address=InetAddress.getByAddress(default_discovery_address);
            port=default_discovery_port;
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
        return this;
    }

    public static class UndesirableDatagramException extends Exception {

        public UndesirableDatagramException() {
            super();
        }

        public UndesirableDatagramException(String message) {
            super(message);
        }

        public UndesirableDatagramException(String message, Throwable cause) {
            super(message, cause);
        }

        public UndesirableDatagramException(Throwable cause) {
            super(cause);
        }

        protected UndesirableDatagramException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class SocketDetails implements Serializable{
        public String address;
        public int port;

        public SocketDetails(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public SocketDetails() {
        }

        public boolean equals(SocketDetails socketDetails){
            if (address.equals(socketDetails.address) && port==socketDetails.port){
                return true;
            }
            return false;
        }
    }

    private SocketDetails socketDetails;

    /*public static void main(String[] args) {
        ServiceInfo a=new ServiceInfo().setDefault();
        a.put("name", "madan");
        a.put("address", "tamghas");

        ServiceInfo b=new ServiceInfo().setDefault();
        b.put("name", "madan");
        b.put("address", "tamghas");
        b.put("school", "wrc");

        System.out.println(a.equals(b));

    }*/

    public ServiceInfo copy(ServiceInfo serviceInfo){
        try {
            address=InetAddress.getByName(serviceInfo.address.getHostName());
            port=serviceInfo.port;
        } catch (UnknownHostException e) {

        }
        for (String key : serviceInfo.arguments.keySet()) {
            arguments.put(key, serviceInfo.get(key));
        }
        return this;
    }
}
