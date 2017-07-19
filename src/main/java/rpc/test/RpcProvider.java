package rpc.test;

import rpc.aop.ProviderHook;
import rpc.aop.ProviderHookImpl;
import rpc.framework.RpcFramework;
import rpc.service.HelloService;
import rpc.service.HelloServiceImpl;

/**
 * Created by lee on 7/18/17.
 */
public class RpcProvider {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("please specify the port ");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        HelloService serviceImpl = new HelloServiceImpl();
        ProviderHook providerHook = new ProviderHookImpl();
        RpcFramework.export(serviceImpl, providerHook, port);
    }
}
