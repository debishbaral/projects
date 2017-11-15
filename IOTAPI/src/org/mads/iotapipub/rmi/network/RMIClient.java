package org.mads.iotapipub.rmi.network;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.mads.iotapipub.rmi.handler.CallHandler;
import org.mads.iotapipub.rmi.handler.CallProxy;
import org.mads.iotapipub.rmi.handler.ConnectionHandler;
import org.mads.iotapipub.rmi.handler.IConnectionHandlerListener;
import org.mads.iotapipub.rmi.presentation.DefaultPresentation;
import org.mads.iotapipub.rmi.presentation.IPresentation;


/**
 * The RMI client.
 * Connects to a RMI RMIServer in a address:port
 * and create local dynamic proxy for description remote
 * methods through a simple interface.
 *
 * 
 * @see    CallHandler
 * @see    RMIServer
 */
public class RMIClient {

	private Socket socket;
	
	private ConnectionHandler connectionHandler;
	private List<IClientListener> listeners = new LinkedList<IClientListener>();
	private final IConnectionHandlerListener connectionHandlerListener = new IConnectionHandlerListener() {
		public void onConnectionClosed() {
			for (IClientListener listener : listeners)
            {
                listener.onDisconnected();
            }
		}
	};

	/**
	 * This creates RMI client to connect to a RMI RMIServer in a address:port and create local dynamic proxy to description remote methods through a simple interface.
	 * @param address address of the server to connect
	 * @param port port of the server
	 * @param callHandler
	 * @throws IOException
     */
	public RMIClient(String address, int port, CallHandler callHandler) throws IOException {
		this(address, port, callHandler, new DefaultPresentation());
	}

	public RMIClient(String address, int port, CallHandler callHandler, IPresentation filter) throws IOException {
		socket = new Socket(address, port);
		connectionHandler = ConnectionHandler.createConnectionHandler(socket, callHandler, filter, connectionHandlerListener);
	}
	
	public void addClientListener(IClientListener listener) {
		listeners.add(listener);
	}
	
	public void removeClientListener(IClientListener listener) {
		listeners.remove(listener);
	}
	
	public void close() throws IOException {
		socket.close();
	}

	/**
	 *
	 * @param interfaceClass Interface.class for the interface implemented by server
	 * @return proxy object for the given interface
     */
	public Object getGlobal(Class<?> interfaceClass) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, new CallProxy(connectionHandler));
	}
}
