package rpc.api.provider;

import rpc.aop.ProviderHook;

/**
 * Created by lee on 7/25/17.
 */
public interface Provider {
    void export(final Object servevice, ProviderHook providerHook, int port);
}
