package org.mads.iotapipub.rmi.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.mads.iotapipub.rmi.description.RemoteInstance;

/**
 * A dynamic proxy which delegates interface
 * calls to a ConnectionHandler
 * 
 * @see       CallHandler
 */
public class CallProxy implements InvocationHandler  {

	private ConnectionHandler connectionHandler;
	
	/**
	 * Create new CallProxy with a ConnectionHandler which will
	 * transport invocations on this Proxy
	 *  
	 * @param connectionHandler
	 */
	public CallProxy(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	
	/**
	 * Delegates description to this proxy to it's ConnectionHandler
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return connectionHandler.remoteInvocation(proxy, method, args);
	}
	
	/**
	 * Build a proxy to a {@see RemoteInstance RemoteInstance}
	 * specifying how it could be reached (i.e., through a ConnectionHandler)
	 * 
	 * @param  remoteInstance
	 * @param  connectionHandler
	 * @return dynamic proxy for RemoteInstance
	 * @throws ClassNotFoundException
	 */
	public static Object buildProxy(RemoteInstance remoteInstance, ConnectionHandler connectionHandler) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(remoteInstance.getClassName());
		return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new CallProxy(connectionHandler));
	}
}
