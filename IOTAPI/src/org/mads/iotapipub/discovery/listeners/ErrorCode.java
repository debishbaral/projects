package org.mads.iotapipub.discovery.listeners;

import org.mads.iotapipub.discovery.implementation.ServiceConsumer;

/**
 * Created by madan on 1/17/17.
 */
public class   ErrorCode {
    public static class FatalError{
        public static final int UNABLE_TO_CREATE_SOCKET_CONNECTION=-100;
        public static final int SERVICE_ALREADY_REGISTERED = -99;
        public static final int UNABLE_TO_CONNECT_THROUGH_SOCKET = -96;
    }

    public static class NonFatalError{
        public static final int SERVICE_NOT_REGISTERED = -98;
        public static final int SERVICE_NOT_FOUND = -97;
    }
}
