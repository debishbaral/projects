package org.mads.iotapipub.rmi.network;

/**
 * Listeners to be used by clients to check connection or disconnection
 */
public interface IClientListener {

	void onDisconnected();
	
}
