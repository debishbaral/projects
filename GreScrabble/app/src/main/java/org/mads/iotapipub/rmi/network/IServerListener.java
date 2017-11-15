package org.mads.iotapipub.rmi.network;

import java.net.Socket;

/**
 *Used by servers to monitor client connection
 */
public interface IServerListener {

	void onClientConnected(Socket socket);

	void onClientDisconnected(Socket socket);
	
}
