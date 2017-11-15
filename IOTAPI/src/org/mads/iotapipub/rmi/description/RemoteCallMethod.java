package org.mads.iotapipub.rmi.description;

/**
 * This class holds method information.
 *
 */
public class RemoteCallMethod implements IRemoteMessage {

	private static final long serialVersionUID = 8463984873987384829L;

	/**
	 * Instance that will receive the description
	 */
	private RemoteInstance remoteInstance;
	
	/**
	 * Method's name
	 */
	private String methodId;
	
	/**
	 * Method's arguments
	 */
	private Object[] args;
	
	/**
	 * The id is a number unique in client and server to identify the description
	 */
	private Long callId;

	public Object[] getArgs() {
		return args;
	}

	public Long getCallId() {
		return callId;
	}

	public RemoteInstance getRemoteInstance() {
		return remoteInstance;
	}

	public String getMethodId() {
		return methodId;
	}

	public RemoteCallMethod(RemoteInstance remoteInstance, String methodId, Object[] args, Long callId) {
		this.remoteInstance = remoteInstance;
		this.methodId = methodId;
		this.args = args;
		this.callId = callId;
	}
	
}
