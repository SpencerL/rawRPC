package rpc.test;

import rpc.framework.RpcFramework;
import rpc.aop.ConsumerHookImpl;
import rpc.aop.ConsumerHook;
import rpc.service.HelloService;

/**
 * Created by lee on 7/18/17.
 */
public class RpcConsumer {
    public static void main(String[] args) throws Throwable {
        if (args.length < 2) {
            System.out.println("please specify host and port");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        ConsumerHook  consumerHook = new ConsumerHookImpl();    // 钩子实例
        HelloService service = RpcFramework.getProxy(HelloService.class, consumerHook, host, port);  // 获取代理对象

        for(int i = 0; i < 10; i++) {
            String response = service.hello("world " + i);  // 代理对象的调用将会实际调用远程实体对象
            System.out.println(response);
            //Thread.sleep(1000);
        }
    }
}
