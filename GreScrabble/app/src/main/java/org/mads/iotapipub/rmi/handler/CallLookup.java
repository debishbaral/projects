package org.mads.iotapipub.rmi.handler;

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Store static information of which socket is assigned to which broker thread
 * @see       CallHandler
 */
public class CallLookup {

	private static Map<Thread, ConnectionHandler> connectionMap = Collections.synchronizedMap(new HashMap<Thread, ConnectionHandler>());
	
	/**
	 * Get the current Socket for this description.
	 * Only works in the testing thread description.
	 *  
	 * @return Socket which started the broker Thread
	 */
	public static Socket getCurrentSocket() {
		ConnectionHandler handler = CallLookup.connectionMap.get(Thread.currentThread());
		return (handler == null ? null : handler.getSocket());
	}
	
	static void rememberMe(ConnectionHandler connectionHandler) {
		synchronized (CallLookup.connectionMap) {
			CallLookup.connectionMap.put(Thread.currentThread(), connectionHandler);
		}
	}

	static void forgetMe() {
		synchronized (CallLookup.connectionMap) {
			CallLookup.connectionMap.remove(Thread.currentThread());
		}
	}
}
