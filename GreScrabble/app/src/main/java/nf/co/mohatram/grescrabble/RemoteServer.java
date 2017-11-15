package nf.co.mohatram.grescrabble;

import org.mads.iotapipub.rmi.description.RemoteInstance;
import org.mads.iotapipub.rmi.exception.RMIException;
import org.mads.iotapipub.rmi.handler.CallHandler;
import org.mads.iotapipub.rmi.network.IServerListener;
import org.mads.iotapipub.rmi.network.RMIServer;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

public class RemoteServer {
    public static final int server_port=34678;
    private CallHandler callHandler;
    private RMIServer rmiServer;
    private IQuestionServer remoteInterface;
    private IServerListener serverListener;
    private boolean serverStarted =false;
    private boolean initializationActive=true;

    public RemoteServer(){
        callHandler=new CallHandler();
        rmiServer=new RMIServer();
    }


    public boolean startServer() throws RMIException, IOException {
        if (serverStarted) return false;
        callHandler.registerGlobal(IQuestionServer.class, this.remoteInterface);
        rmiServer.addServerListener(serverListener);
        rmiServer.bind(server_port, callHandler);
        return true;
    }

    public void close(){
        rmiServer.removeServerListener(serverListener);
        rmiServer.close();
        serverStarted =false;
    }

    public void setRemoteInterface(IQuestionServer remoteObject) {
        this.remoteInterface = remoteObject;
        Set<RemoteInstance> remoteInstances = this.callHandler.getExportedObjects().keySet();
        RemoteInstance key=null;
        for(RemoteInstance ri: remoteInstances){
            if (ri.getClassName().contains(IQuestionServer.class.getName())){
                key=ri;
            }
        }
        if (key!=null)this.callHandler.getExportedObjects().put(key, remoteObject);
    }

    public void setServerListener(IServerListener serverListener) {
        this.serverListener = serverListener;

    }

    public boolean isInitializationActive() {
        return initializationActive;
    }

    public void clearExports() {
        callHandler.getExportedObjects().clear();
    }
}
