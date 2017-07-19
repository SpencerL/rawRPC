package rpc.aop;

import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by lee on 7/19/17.
 */
public class ProviderHookImpl implements ProviderHook {
    public void connectionInfo(Socket client, Object service, Method method) {
        System.out.println("Connection from: " + client.getInetAddress());
        System.out.println("Requested Service: " + service.getClass().getName());
        System.out.println("Request Method: " + method.getName()+"()");

    }
}
