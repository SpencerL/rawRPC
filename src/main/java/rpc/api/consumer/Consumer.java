package rpc.api.consumer;

import rpc.aop.ConsumerHook;

/**
 * Created by lee on 7/25/17.
 */
public interface Consumer {
    Object getProxy(final Class<?> interfaceClazz, final ConsumerHook consumerHook,
                    final String host, final int port) throws Throwable;
}
