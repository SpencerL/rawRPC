package rpc.api.consumer;

import rpc.aop.ConsumerHook;
import rpc.codec.JavaCodec;
import rpc.model.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.Arrays;


/**
 * 对应于　SelProvider
 * Created by lee on 7/25/17.
 */
public class SelConsumer implements  Consumer{
    public Object getProxy(final Class<?> interfaceClazz, final ConsumerHook consumerHook,
                           final String host, final  int port) throws Throwable {

        if (interfaceClazz == null)
            throw new IllegalArgumentException("interface class is null");
        if (!interfaceClazz.isInterface())
            throw new IllegalArgumentException("given class is not an interface");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port");
        System.out.println("Getting remote service " + interfaceClazz.getName() + " from " +
                host+":" +port);
        Object proxy = Proxy.newProxyInstance(interfaceClazz.getClassLoader(),
                new Class<?>[]{interfaceClazz}, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = new Socket(host, port);
                        OutputStream out = null;
                        InputStream in = null;
                        Object result = null;
                        try {
                            Request request = new Request(method.getName(), method.getParameterTypes(),
                                    args);
                            byte[] sendBytes = JavaCodec.encode(request);
                            out = socket.getOutputStream();
                            out.write(sendBytes);

                            in = socket.getInputStream();
                            byte[] recvBuf = new byte[100];
                            int length = in.read(recvBuf);
                            byte[] msgBytes = Arrays.copyOfRange(recvBuf, 0, length);
                            result = JavaCodec.decode(msgBytes);
                            socket.close();
                        } catch (IOException e) {

                        }
                        finally {
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {}
                            }
                            if (in != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {}
                            }
                        }
                        return result;
                    }
                });

        return proxy;
    }
}















