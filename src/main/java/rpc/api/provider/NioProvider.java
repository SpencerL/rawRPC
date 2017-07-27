package rpc.api.provider;

import rpc.aop.ProviderHook;
import rpc.api.handler.Worker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于Selector和线程池的实现，
 * Created by lee on 7/26/17.
 */
public class NioProvider {
    private Selector selector;
    private ServerSocketChannel ssc;
    private int port;
    private int clientTimeout;
    private ExecutorService exec;
    private Object service;
    private ProviderHook providerHook;

    public NioProvider(int port, Object service, ProviderHook providerHook, int clientTimeout) {
        this.port = port;
        this.service = service;
        this.providerHook = providerHook;
        this.clientTimeout =  clientTimeout;    // 客户端连接超时
        init();
    }
    /*
    public void export(final Object service, ProviderHook providerHook, int port) {
        if (service == null)
            throw new IllegalArgumentException("given service is null");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("invalid port");
        this.port = port;
        System.out.println("Export service " +service.getClass().getName() + " on port " + port);
    }
    */

    public void init() {
        try {
            exec = Executors.newFixedThreadPool(3);
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(port));
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Export service " + service.getClass().getName() + " on port " + port);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("start listen on " + port);
        while (true) {
            try {   // 多少秒内没有连接接入，服务器停止运行
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();
                    SocketChannel sc = ssc.accept();
                    iter.remove();
                    exec.submit(new Worker(sc, service, providerHook, clientTimeout));  //Worker线程有超时设置
                }

            } catch (IOException e) { e.printStackTrace();}

        }

    }
}
