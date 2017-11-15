package org.mads.iotapipub.rmi.presentation;

import org.mads.iotapipub.rmi.description.IRemoteMessage;

/**
 * Default presentation implementation.
 * Only forwards the data.
 */
public class DefaultPresentation implements IPresentation {

	public IRemoteMessage readObject(Object obj) {
		return (IRemoteMessage) obj;
	}

	public Object prepareWrite(IRemoteMessage message) {
		return message;
	}

}
