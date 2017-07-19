package rpc.aop;

import java.lang.reflect.Method;

/**
 * Created by lee on 7/19/17.
 */
public interface ConsumerHook {
    void before(Method method);
    void after(Method method);
}
