package org.mads.iotapipub.rmi.presentation;

import org.mads.iotapipub.rmi.description.IRemoteMessage;

/**
 * A interface to
 * intercept messages and make any needed modification just before sending and just after receiving data.
 * (ie. encryption / compression).<br></br>
 * It is important that both client and server to pass same interface definition object during RMI description
 */
public interface IPresentation {
	
	IRemoteMessage readObject(Object obj);

	Object prepareWrite(IRemoteMessage message);
	
}
