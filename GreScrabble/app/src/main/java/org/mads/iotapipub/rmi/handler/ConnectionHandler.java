package org.mads.iotapipub.rmi.handler;

import org.mads.iotapipub.rmi.description.IRemoteMessage;
import org.mads.iotapipub.rmi.description.RemoteCallMethod;
import org.mads.iotapipub.rmi.description.RemoteInstance;
import org.mads.iotapipub.rmi.description.RemoteReturn;
import org.mads.iotapipub.rmi.exception.RMIException;
import org.mads.iotapipub.rmi.network.RMIClient;
import org.mads.iotapipub.rmi.network.RMIServer;
import org.mads.iotapipub.rmi.presentation.DefaultPresentation;
import org.mads.iotapipub.rmi.presentation.IPresentation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * A ConnectionHandler is object which can description remote
 * methods, receive remote calls and dispatch its returns.
 *
 * @see CallHandler
 * @see RemoteInstance
 * @see RemoteCallMethod
 * @see RemoteReturn
 * @see RMIClient
 * @see RMIServer
 * @see DefaultPresentation
 */
public class ConnectionHandler implements Runnable {

    private static AtomicLong callId = new AtomicLong(00L);

    private CallHandler callHandler;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private IPresentation presentation;
    private List<IConnectionHandlerListener> listeners = new LinkedList<IConnectionHandlerListener>(); //connection handlers
    private Map<RemoteInstance, Object> remoteInstanceProxies = new HashMap<RemoteInstance, Object>(); //one object one remote instance
    private List<RemoteReturn> remoteReturns = new LinkedList<RemoteReturn>();

    private ConnectionHandler(Socket socket, CallHandler callHandler, IPresentation presentation) {
        this.callHandler = callHandler;
        this.socket = socket;
        this.presentation = presentation;
    }

    /**
     * Get instance of Connection handler
     * @param socket       socket connection between client and server
     * @param callHandler
     * @param presentation the data presentation layer to use
     * @return instance of connection handler that is running inside thread named ConnectionHandler
     */
    public static ConnectionHandler createConnectionHandler(Socket socket, CallHandler callHandler, IPresentation presentation) {

        ConnectionHandler connectionHandler = new ConnectionHandler(socket, callHandler, presentation);
        String threadName = String.format("ConnectionHandler (%s:%d)", socket.getInetAddress().getHostAddress(), socket.getPort());
        Thread connectionHandlerThread = new Thread(connectionHandler, threadName);
        connectionHandlerThread.setDaemon(true);

        connectionHandlerThread.start();
        return connectionHandler;
    }

    /**
     * @param socket                     socket socket connection between client and server
     * @param callHandler                   Call handler to use
     * @param presentation               the data presentation layer to use
     * @param iConnectionHandlerListener connection handler listener to use inside this Connection handler instance
     * @return instance of connection handler that is running inside thread named ConnectionHandler(address:port)
     */
    public static ConnectionHandler createConnectionHandler(Socket socket, CallHandler callHandler, IPresentation presentation, IConnectionHandlerListener iConnectionHandlerListener) {
        ConnectionHandler connectionHandler = ConnectionHandler.createConnectionHandler(socket, callHandler, presentation);
        connectionHandler.addConnectionHandlerListener(iConnectionHandlerListener);
        return connectionHandler;
    }

    public void addConnectionHandlerListener(IConnectionHandlerListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionHandlerListener(IConnectionHandlerListener listener) {
        listeners.remove(listener);
    }

    public void run() {
        ObjectInputStream input=null;

        try {
            input = new ObjectInputStream(socket.getInputStream());

            while (socket.isConnected()) {
                Object objFromStream = input.readUnshared();

                IRemoteMessage remoteMessage = presentation.readObject(objFromStream);

                if (remoteMessage instanceof RemoteCallMethod) {

                    final RemoteCallMethod remoteCallMethod = (RemoteCallMethod) remoteMessage;
                    if (remoteCallMethod.getArgs() != null) {
                        for (int n = 0; n < remoteCallMethod.getArgs().length; n++) {
                            Object arg = remoteCallMethod.getArgs()[n];
                            if (arg instanceof RemoteInstance) {
                                RemoteInstance remoteInstance = (RemoteInstance) arg;
                                remoteCallMethod.getArgs()[n] = getProxyFromRemoteInstance(remoteInstance);
                            }
                        }
                    }

                    Thread brokerThread = new Thread(new Runnable() {
                        public void run() {
                            CallLookup.rememberMe(ConnectionHandler.this);

                            RemoteReturn remoteReturn;
                            try {
                                remoteReturn = callHandler.delegateCall(remoteCallMethod);
                                sendMessage(remoteReturn);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            CallLookup.forgetMe();
                        }
                    }, "Broker");
                    brokerThread.setDaemon(true);
                    brokerThread.start();
                } else if (remoteMessage instanceof RemoteReturn) {
                    RemoteReturn remoteReturn = (RemoteReturn) remoteMessage;
                    synchronized (remoteReturns) {
                        remoteReturns.add(remoteReturn);
                        remoteReturns.notifyAll();
                    }
                } else
                    throw new RMIException("Unknown IRemoteMessage type");
            }
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
            }

            synchronized (remoteReturns) {
                remoteReturns.notifyAll();
            }

            for (IConnectionHandlerListener listener : listeners) {
                listener.onConnectionClosed();
            }
        }
    }

    private synchronized void sendMessage(IRemoteMessage remoteMessage) throws IOException {
        if (objectOutputStream == null) {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }

        Object objToWrite = presentation.prepareWrite(remoteMessage);
        objectOutputStream.reset();
        objectOutputStream.writeUnshared(objToWrite);
        objectOutputStream.flush();
    }


    final synchronized Object remoteInvocation(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Long id = callId.getAndIncrement();

        RemoteInstance remoteInstance;
        remoteInstance = getRemoteInstanceFromProxy(proxy);
        if (remoteInstance == null) {
            remoteInstance = new RemoteInstance(null, proxy.getClass().getInterfaces()[0].getName());
        }

        if (args != null) {
            for (int n = 0; n < args.length; n++) {
                RemoteInstance remoteRef = callHandler.getRemoteReference(args[n]);
                if (remoteRef != null)
                    args[n] = remoteRef;
            }
        }

        String methodId = method.toString().substring(15);
        System.out.println(methodId);

        IRemoteMessage remoteCall = new RemoteCallMethod(remoteInstance, methodId, args, id);
        sendMessage(remoteCall);

        RemoteReturn remoteReturn = null;

        boolean bReturned = false;
        do {
            synchronized (remoteReturns) {
                for (RemoteReturn ret : remoteReturns) {
                    if (ret.getCallId().equals(id)) {
                        bReturned = true;
                        remoteReturn = ret;
                        break;
                    }
                }
                if (bReturned) {
                    remoteReturns.remove(remoteReturn);
                } else {
                    try {
                        remoteReturns.wait();
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
        while (socket.isConnected() && !bReturned);

        if (!socket.isConnected() && !bReturned) {
            throw new RMIException("Connection aborted");
        }

        if (remoteReturn.isReturnThrowable() && remoteReturn.getRet() instanceof Throwable) {
            throw ((Throwable) remoteReturn.getRet()).getCause();
        }

        if (remoteReturn.getRet() instanceof RemoteInstance) {
            RemoteInstance remoteInstanceReturn = (RemoteInstance) remoteReturn.getRet();
            Object proxyReturn = remoteInstanceProxies.get(remoteInstanceReturn);
            if (proxyReturn == null) {
                proxyReturn = CallProxy.buildProxy(remoteInstanceReturn, this);
                remoteInstanceProxies.put(remoteInstanceReturn, proxyReturn);
            }
            return proxyReturn;
        }

        return remoteReturn.getRet();
    }

    private Object getProxyFromRemoteInstance(RemoteInstance remoteInstance) {
        Object proxy = remoteInstanceProxies.get(remoteInstance);
        if (proxy == null) {
            try {
                proxy = CallProxy.buildProxy(remoteInstance, this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            remoteInstanceProxies.put(remoteInstance, proxy);
        }
        return proxy;
    }

    private RemoteInstance getRemoteInstanceFromProxy(Object proxy) {
        for (RemoteInstance remoteInstance : remoteInstanceProxies.keySet()) {
            if (remoteInstanceProxies.get(remoteInstance) == proxy)
                return remoteInstance;
        }

        return null;
    }

    public Socket getSocket() {
        return socket;
    }
}
