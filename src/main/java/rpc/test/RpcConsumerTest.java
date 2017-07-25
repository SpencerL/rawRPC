package rpc.test;

import rpc.aop.ConsumerHookImpl;
import rpc.aop.ConsumerHook;
import rpc.api.consumer.SelConsumer;
import rpc.service.HelloService;
import rpc.api.consumer.Consumer;


/**
 * Created by lee on 7/18/17.
 */
public class RpcConsumerTest {
    public static void main(String[] args) throws Throwable {
        if (args.length < 2) {
            System.out.println("please specify host and port");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        ConsumerHook  consumerHook = new ConsumerHookImpl();    // 钩子实例

        // Selector异步测试
        Consumer consumer = new SelConsumer();
        HelloService service = (HelloService) consumer.getProxy(HelloService.class, consumerHook, host, port);

        // Bio同步测试
        //Consumer consumer = new BioConsumer();
        //HelloService service = (HelloService) consumer.getProxy(HelloService.class, consumerHook, host, port);



        for(int i = 0; i < 10; i++) {
            String response = service.hello("world " + i);  // 代理对象的调用将会实际调用远程实体对象
            System.out.println(response.toString());
            Thread.sleep(1000);
        }

    }
}
