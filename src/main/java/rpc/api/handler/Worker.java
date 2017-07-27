package rpc.api.handler;

import rpc.aop.ProviderHook;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lee on 7/26/17.
 */
public class Worker implements Runnable {
    private Selector  selector;
    private SocketChannel sc;
    private int timeout;
    private Object service;
    private ProviderHook providerHook;
    public Worker(SocketChannel sc, Object service, ProviderHook providerHook, int timeout) {
        this.sc = sc;
        this.timeout = timeout;
        this.service = service;
        this.providerHook = providerHook;
        init();
    }
    public void init() {
        try {
            selector = Selector.open();
            sc.configureBlocking(false);
            SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
            key.attach(new RequestHandler(service, providerHook));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        while (true) {
            try {   //　超时设置
                long start = System.currentTimeMillis();
                selector.select(timeout);
                if (System.currentTimeMillis() - start > timeout) {
                    byte[] timeoutMsg = "Time out!!, socketchannel closed by server\n".getBytes();
                    ByteBuffer buf =  ByteBuffer.allocate(100);
                    buf.put(timeoutMsg);
                    buf.flip();
                    sc.write(buf);
                    sc.close();
                    break;  // 读写线程线程终止
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    RequestHandler handler = (RequestHandler) key.attachment();
                    handler.operate(key);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
