package org.mads.iotapipub.rmi.exception;

/**
 * Common RMI exception
 */
public class RMIException extends Exception {

	private static final long serialVersionUID = -1034597384628389273L;

	public RMIException() {
		super();
	}

	public RMIException(String message, Throwable cause) {
		super(message, cause);
	}

	public RMIException(String message) {
		super(message);
	}

	public RMIException(Throwable cause) {
		super(cause);
	}
}
