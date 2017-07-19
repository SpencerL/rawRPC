package rpc.aop;

import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by lee on 7/19/17.
 */
public interface ProviderHook {
    void connectionInfo(Socket client, Object service, Method method);

}
