package org.mads.iotapipub.rmi.network;

import org.mads.iotapipub.rmi.handler.CallHandler;
import org.mads.iotapipub.rmi.handler.ConnectionHandler;
import org.mads.iotapipub.rmi.handler.IConnectionHandlerListener;
import org.mads.iotapipub.rmi.presentation.DefaultPresentation;
import org.mads.iotapipub.rmi.presentation.IPresentation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


/**
 * The RMI server.
 * This object listen to a specific port and
 * when a client connects it delegates the connection
 * to a {@link ConnectionHandler ConnectionHandler}.
 *
 * @see CallHandler
 * @see RMIClient
 */
public class RMIServer {

    private ServerSocket serverSocket;

    private boolean enabled;

    private List<IServerListener> listeners = new LinkedList<IServerListener>();

    public void addServerListener(IServerListener listener) {
        listeners.add(listener);
    }

    public void removeServerListener(IServerListener listener) {
        listeners.remove(listener);
    }

    public void close() {
        enabled = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            //Oops unable to close server socket
        }
    }

    public void bind(int port, CallHandler callHandler) throws IOException {
        bind(port, callHandler, new DefaultPresentation());
    }


    public void bind(final int port, final CallHandler callHandler, final IPresentation presentation) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.setPerformancePreferences(1, 1, 100); //ratio for connection time , latency and bandwidth
        enabled = true;

        serverSocket.bind(new InetSocketAddress(port));

        Thread serverThread = new Thread(new Runnable() {
            public void run() {
                while (enabled) {
                    Socket acceptSocket = null;
                    try {
                        acceptSocket = serverSocket.accept();

                        final Socket clientSocket = acceptSocket;
                        ConnectionHandler.createConnectionHandler(clientSocket,
                                callHandler,
                                presentation,
                                new IConnectionHandlerListener() {

                                    public void onConnectionClosed() {
                                        for (IServerListener listener : listeners) {
                                            listener.onClientDisconnected(clientSocket);
                                        }
                                    }

                                });
                        for (IServerListener listener : listeners) {
                            listener.onClientConnected(clientSocket);
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        }, String.format("Bind (%d)", port));
        serverThread.start();
    }

}
