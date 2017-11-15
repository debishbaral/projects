package org.mads.iotapipub.discovery.exception;

/**
 * Created by madan on 1/15/17.
 */
public class UndesirableDatagramException extends Exception {
    public UndesirableDatagramException() {
    }

    public UndesirableDatagramException(String message) {
        super(message);
    }

    public UndesirableDatagramException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndesirableDatagramException(Throwable cause) {
        super(cause);
    }

    public UndesirableDatagramException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
