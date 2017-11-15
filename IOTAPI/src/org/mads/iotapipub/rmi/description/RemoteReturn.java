package org.mads.iotapipub.rmi.description;

/**
 * Class that holds method return information.
 */
public class RemoteReturn implements IRemoteMessage {

	private static final long serialVersionUID = -2448494782081018830L;

	/**
	 * The return is a throwable to be thrown?
	 */
	protected boolean isReturnThrowable;
	
	/**
	 *object to return
	 */
	protected Object ret;
	
	/**
	 * Call id which generated this return
	 */
	protected Long callId;

	public Long getCallId() {
		return callId;
	}

	public Object getRet() {
		return ret;
	}

	public boolean isReturnThrowable() {
		return isReturnThrowable;
	}

	public RemoteReturn(boolean isReturnThrowable, Object ret, Long callId) {
		this.isReturnThrowable = isReturnThrowable;
		this.ret = ret;
		this.callId = callId;
	}
}
