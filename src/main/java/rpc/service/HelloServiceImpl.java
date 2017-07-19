package rpc.service;

import rpc.service.HelloService;

/**
 * Created by lee on 7/18/17.
 */
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "hello " + name;
    }
}
