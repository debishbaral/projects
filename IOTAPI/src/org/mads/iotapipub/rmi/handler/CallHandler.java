package org.mads.iotapipub.rmi.handler;

import org.mads.iotapipub.rmi.description.RemoteCallMethod;
import org.mads.iotapipub.rmi.description.RemoteInstance;
import org.mads.iotapipub.rmi.description.RemoteReturn;
import org.mads.iotapipub.rmi.exception.RMIException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A handler who has information of  a RemoteInstance and its
 * local implementations.
 * Local implementation objects must register with
 * methods {@link CallHandler#registerGlobal registerGlobal} and
 * {@link CallHandler#exportObject exportObject} to work remotely.
 *
 * @see RemoteInstance
 */
public class CallHandler {

    private Map<RemoteInstance, Object> exportedObjects = new HashMap<RemoteInstance, Object>();

    /**
     * Generate {@link Class} array from respective objects
     *
     * @param objects
     * @return array of {@link Class} objects
     */
    public static Class<?>[] typeFromObjects(Object[] objects) {
        Class<?>[] argClasses = null;
        if (objects != null) {
            argClasses = new Class[objects.length];
            for (int n = 0; n < objects.length; n++) {
                Object obj = objects[n];
                argClasses[n++] = obj.getClass();
            }
        }
        return argClasses;
    }

    /**
     * Register an object as global implementation of cInterface
     *
     * @param cInterface
     * @param objImplementation
     * @throws RMIException
     */
    public void registerGlobal(Class<?> cInterface, Object objImplementation) throws RMIException {
        exportObject(cInterface, objImplementation, null);
    }

    /**
     * Register an object as an implementation(one of many) of cInterface
     *
     * @param cInterface
     * @param exportedObject
     * @throws RMIException
     */
    public void exportObject(Class<?> cInterface, Object exportedObject) throws RMIException {
        UUID objUUID = UUID.randomUUID();
        String instanceId = objUUID.toString();
        exportObject(cInterface, exportedObject, instanceId);
    }


    private void exportObject(Class cInterface, Object objImplementation, String instanceId) throws RMIException {
        if (!cInterface.isAssignableFrom(objImplementation.getClass())) {
            throw new RMIException(String.format("Class %s is not assignable from %s", objImplementation.getClass().getName(), cInterface.getName()));
        }

        for (RemoteInstance remoteInstance : exportedObjects.keySet()) {
            if ((remoteInstance.getInstanceId() == instanceId || (remoteInstance.getInstanceId() != null && remoteInstance.getInstanceId().equals(instanceId))) && remoteInstance.getClassName().equals(cInterface.getName())) {
                throw new RMIException(String.format("Interface %s already has an implementation class", cInterface.getName()));
            }
        }

        RemoteInstance remoteInstance = new RemoteInstance(instanceId, cInterface.getName());
        exportedObjects.put(remoteInstance, objImplementation);
    }

    /**
     * Make description using this method.
     * Client calls to connection handler which calls this method to perform RMI
     *
     * @param remoteCallMethod
     * @return
     * @throws RMIException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public RemoteReturn delegateCall(RemoteCallMethod remoteCallMethod) throws
            RMIException,
            SecurityException,
            NoSuchMethodException,
            IllegalArgumentException,
            IllegalAccessException {
        Object implementor = exportedObjects.get(remoteCallMethod.getRemoteInstance());
        if (implementor == null) {
            throw new RMIException(String.format("Class %s doesn't have implementation", remoteCallMethod.getRemoteInstance().getClassName()));
        }

        RemoteReturn remoteReturn;

        Method implementationMethod = null;

        for (Method method : implementor.getClass().getMethods()) {
            String implementationMethodId = method.toString();
            implementationMethodId = implementationMethodId.replace(implementor.getClass().getName(), remoteCallMethod.getRemoteInstance().getClassName());

            if (implementationMethodId.endsWith(remoteCallMethod.getMethodId())) {
                implementationMethod = method;
                break;
            }
        }

        if (implementationMethod == null) {
            throw new NoSuchMethodException(remoteCallMethod.getMethodId());
        }

        try {
            Object methodReturn = null;
            implementationMethod.setAccessible(true);
            methodReturn = implementationMethod.invoke(implementor, remoteCallMethod.getArgs());
            if (exportedObjects.containsValue(methodReturn)) {
                methodReturn = getRemoteReference(methodReturn);
            }

            remoteReturn = new RemoteReturn(false, methodReturn, remoteCallMethod.getCallId());
        } catch (InvocationTargetException e) {
            remoteReturn = new RemoteReturn(true, e, remoteCallMethod.getCallId());
        }

        return remoteReturn;
    }

    /**
     * Get remote instance object implementation information
     *
     * @param obj
     * @return Return the remote reference of the object
     */
    public RemoteInstance getRemoteReference(Object obj) {
        for (RemoteInstance remoteInstance : exportedObjects.keySet()) {
            Object exportedObj = exportedObjects.get(remoteInstance);
            if (exportedObj == obj) {
                return remoteInstance;
            }
        }
        return null;
    }
}
