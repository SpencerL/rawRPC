package rpc.api.consumer;

import rpc.aop.ConsumerHook;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * Created by lee on 7/25/17.
 */
public class BioConsumer implements Consumer{
    public Object getProxy(final Class<?> interfaceClazz, final ConsumerHook consumerHook,
                           final String host, final int port) throws Throwable{
        if (interfaceClazz == null)
            throw new IllegalArgumentException("interface is null");
        if (!interfaceClazz.isInterface())
            throw new IllegalArgumentException(interfaceClazz.getName() + " is not an interface ");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port");
        System.out.println("Getting " + interfaceClazz.getName() + " from " +
                host+":" + port);
        Object proxy = Proxy.newProxyInstance(interfaceClazz.getClassLoader(),
                new Class<?>[]{interfaceClazz}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        ObjectInputStream input = null;
                        ObjectOutputStream output = null;
                        Object result = null;
                        try {
                            Socket socket = new Socket(host, port);
                            output =  new ObjectOutputStream(socket.getOutputStream());
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            // 等待返回结果
                            input = new ObjectInputStream(socket.getInputStream());
                            result  = input.readObject();
                            socket.close();
                        } catch(IOException e) {}
                        finally {
                            if (input != null) {
                                try {
                                    input.close();
                                } catch (IOException e) {}
                            }
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException e) {}
                            }
                        }
                        return result;
                    }
                });

        return proxy;

    }
}
