package org.mads.iotapipub.rmi.description;

import org.mads.iotapipub.rmi.handler.CallHandler;

import java.io.Serializable;

/**
 * Class that holds information about a remote instance,making the instance unique in all remote JVM.
 * All remote instances must have a unique random UUID,
 * except the global ones (registered with {@link CallHandler#registerGlobal CallHandler})
 */

public class RemoteInstance implements Serializable {

	private static final long serialVersionUID = -2739386129397308339L;

	protected String instanceId;
	
	protected String className;

	public String getClassName() {
		return className;
	}
	
	public String getInstanceId() {
		return instanceId;
	}

	public RemoteInstance(String instanceId, String className) {
		this.instanceId = instanceId;
		this.className = className;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteInstance) {
			RemoteInstance remoteInstance = (RemoteInstance) obj;
			boolean instanceId = (getInstanceId() == remoteInstance.getInstanceId() || (getInstanceId() != null && getInstanceId().equals(remoteInstance.getInstanceId())));
			boolean className = (getClassName().equals(remoteInstance.getClassName()));
			return (className && instanceId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return className.hashCode();
	}
	
}
