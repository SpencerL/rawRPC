package rpc.aop;

import java.lang.reflect.Method;

/**
 * Created by lee on 7/19/17.
 */
public class ConsumerHookImpl implements ConsumerHook {
    public void before(Method method) {
        long begin = System.currentTimeMillis();
        System.out.println(method.getName()+ "() begin: " + begin);
    }
    public void after(Method method) {
        long end = System.currentTimeMillis();
        System.out.println(method.getName() + "() end: " + end);
    }
}
