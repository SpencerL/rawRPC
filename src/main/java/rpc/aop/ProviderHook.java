package rpc.aop;


import java.net.Socket;

/**
 * Created by lee on 7/19/17.
 */
public interface ProviderHook {
    void requestInfo(Socket client, Object service, String methodName);

}
