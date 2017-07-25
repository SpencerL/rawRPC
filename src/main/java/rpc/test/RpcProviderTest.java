package rpc.test;

import rpc.aop.ProviderHook;
import rpc.aop.ProviderHookImpl;
import rpc.api.provider.BioProvider;
import rpc.api.provider.Provider;
import rpc.api.provider.SelProvider;
import rpc.service.HelloService;
import rpc.service.HelloServiceImpl;

/**
 * Created by lee on 7/18/17.
 */
public class RpcProviderTest {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("please specify the port ");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        HelloService serviceImpl = new HelloServiceImpl();
        ProviderHook providerHook = new ProviderHookImpl();

        // Selector 异步测试
        Provider providerImpl = new SelProvider();

        // Bio 同步阻塞测试
        //Provider providerImpl = new BioProvider();

        providerImpl.export(serviceImpl, providerHook, port);

    }
}
