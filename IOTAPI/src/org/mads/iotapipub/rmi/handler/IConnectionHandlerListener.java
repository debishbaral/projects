package org.mads.iotapipub.rmi.handler;

/**
 * This listener can be used to monitor a ConnectionHandler to know when it finishes

 * @see	   ConnectionHandler
 */

public interface IConnectionHandlerListener {
	void onConnectionClosed();
}
