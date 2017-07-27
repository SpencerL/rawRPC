package rpc.api.handler;

import rpc.aop.ProviderHook;
import rpc.codec.JavaCodec;
import rpc.model.Request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by lee on 7/26/17.
 */
public class RequestHandler {
    private ByteBuffer buf = ByteBuffer.allocate(1024);
    private Object service;
    private ProviderHook providerHook;
    String methodName = null;
    Class<?>[] params = null;
    Object[] args = null;

    public RequestHandler(Object service, ProviderHook providerHook) {
        this.service = service;
        this.providerHook = providerHook;
    }
    public void operate(SelectionKey key) {
        SocketChannel sc = (SocketChannel)key.channel();

        if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
            buf.clear();
            try {
                sc.read(buf);
                byte[] recvBytes = buf.array();
                Request request = (Request) JavaCodec.decode(recvBytes);
                methodName = request.getMethodName();
                params = request.getParameterTypes();
                args = request.getArguments();
                providerHook.requestInfo(sc.socket(), service, methodName); // provider hook

                key.interestOps(SelectionKey.OP_WRITE);
            } catch(IOException e) { e.printStackTrace();}
        }
        else if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
            buf.clear();
            try {
                Method method = service.getClass().getMethod(methodName, params);
                Object result = method.invoke(service, args);
                byte[] sendBytes = JavaCodec.encode(result);
                buf.put(sendBytes);
                buf.flip();
                sc.write(buf);

                key.interestOps(SelectionKey.OP_READ);
            } catch(IOException e) {
                e.printStackTrace();
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


        }
    }
}
