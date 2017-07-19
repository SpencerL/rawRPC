package rpc.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;


import rpc.aop.ConsumerHook;
import rpc.aop.ProviderHook;


/**
 * Created by lee on 7/18/17.
 */
public class RpcFramework {
    //final Object service;
    //int port;
    /*
    public RpcFramework(Object service, int port) {
        this.service = service;
        this.port = port;
    }
    */

    /**
     * 向外暴露端口和提供的服务　
     */
    public static  void export(final Object service, ProviderHook providerHook, int port) {
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


    public static <T> T getProxy(final Class<T> interfaceClass, final ConsumerHook consumerHook,
                                 final String host, final int port) throws Throwable {
        if (interfaceClass == null)
            throw new IllegalArgumentException("Interface class is null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " is not an interface");
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("Host is null or empty");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException(port + " is invalid");

        System.out.println("Get remote service " + interfaceClass.getName() + " from " +
                "server " + host +":" + port);

        // 获取代理对象
        Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = new Socket(host, port);
                        ObjectInputStream input = null;
                        ObjectOutputStream output = null;
                        Object result = null;
                        consumerHook.before(method);

                        try {
                            output = new ObjectOutputStream(socket.getOutputStream());
                            // 将要调用的方法名，从参数类型，参数值写入socket,远程发送到服务端
                            output.writeUTF(method.getName());
                            output.writeObject(method.getParameterTypes());
                            output.writeObject(args);

                            // 尝试获取结果
                            input = new ObjectInputStream(socket.getInputStream());
                            result = input.readObject();

                            consumerHook.after(method); //
                            if (result instanceof  Throwable) {
                                // 收到的是异常
                                throw (Throwable) result;
                            }
                            //return result;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            if (input != null) {
                                try {
                                    input.close();
                                } catch (Exception e) {}
                            }
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (Exception e) {}
                            }
                            socket.close();
                        }
                        return result;
                    }
                });

        return (T)proxy;

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
            providerHook.connectionInfo(socket, service, method);

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
