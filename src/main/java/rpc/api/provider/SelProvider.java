package rpc.api.provider;

import rpc.aop.ProviderHook;
import rpc.codec.JavaCodec;
import rpc.model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lee on 7/25/17.
 */
public class SelProvider implements Provider{
    public void export(final Object service, ProviderHook providerHook, int port) {
        if (service == null)
            throw new IllegalArgumentException("service is null");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port");
        System.out.println("Export service "+ service.getClass().getName()+" on: " + port);

        String methodName = null;
        Class<?>[] parameterTypes = null;
        Object[] arguments = null;
        ServerSocketChannel ssc = null;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            Selector  selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(port);
            ServerSocket  ss = ssc.socket();
            ss.bind(address);
            // 注册连接事件
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();  // 会阻塞当前线程，知道直到至少有一个事件发生
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        SocketChannel sc = ssc.accept();    // 不会阻塞，因为已经准备好了
                        sc.configureBlocking(false);    // 配置为非阻塞
                        sc.register(selector, SelectionKey.OP_READ);    // 注册读ready 事件　

                        it.remove();
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        SocketChannel sc = (SocketChannel)key.channel();

                        // 获取request
                        buffer.clear();
                        sc.read(buffer);
                        byte[] recvBytes = buffer.array();
                        Request  request = (Request)JavaCodec.decode(recvBytes);
                        methodName = request.getMethodName();
                        parameterTypes = request.getParameterTypes();
                        arguments = request.getArguments();
                        providerHook.requestInfo(sc.socket(), service, methodName);  // provider钩子
                        sc.register(selector, SelectionKey.OP_WRITE);
                        it.remove();

                    } else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
                        SocketChannel sc = (SocketChannel)key.channel();
                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                        Object result = method.invoke(service, arguments);
                        byte[] sendBytes = JavaCodec.encode(result);
                        buffer.clear();
                        buffer.put(sendBytes);
                        buffer.flip();
                        sc.write(buffer);
                        it.remove();
                        sc.close(); // 没有关socket导致 connect by remote peer bu
                    }

                }
            }
        }
        catch (IOException e) { e.printStackTrace();}
        catch (NoSuchMethodException e) {e.printStackTrace();}
        catch (IllegalAccessException e) {e.printStackTrace();}
        catch (InvocationTargetException e) {e.printStackTrace();}
        finally {
            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException e) {}
            }
        }
    }

}














