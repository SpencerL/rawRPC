package rpc.api.provider;

import rpc.aop.ProviderHook;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lee on 7/25/17.
 */
public class BioProvider implements  Provider {
    /**
     *暴露服务
     */
    public void export(final Object service, ProviderHook providerHook, int port) {
        if (service == null)
            throw new IllegalArgumentException("service instance is null");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port: " + port);
        System.out.println("Export service " + service.getClass().getName() +
                " on port " + port);
        try {
            ServerSocket serverSocket  = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new RequestHandler(socket, providerHook, service)).start();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }

    }
}


class RequestHandler implements Runnable {
    private Socket socket;
    private ProviderHook providerHook;
    private Object service;
    public RequestHandler(Socket socket, ProviderHook providerHook, Object service) {
        this.socket = socket;
        this.service =  service;
        this.providerHook = providerHook;
    }
    public void run() {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            // 获取方法名，参数类型，参数列表
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[])input.readObject();
            Object[] arguments = (Object[]) input.readObject();

            output = new ObjectOutputStream(socket.getOutputStream());
            // 获取方法实体
            Method method = service.getClass().getMethod(methodName, parameterTypes);

            // 服务端钩子
            providerHook.requestInfo(socket, service, methodName);

            // 获取调用结果
            Object result = method.invoke(service, arguments);

            // 将调用结果写入socket
            output.writeObject(result);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {e.printStackTrace();}
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) { e.printStackTrace();}
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) { e.printStackTrace();}
            }
        }
    }
}
