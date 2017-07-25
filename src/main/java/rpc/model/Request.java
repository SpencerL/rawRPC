package rpc.model;

import java.io.Serializable;

/**
 * Created by lee on 7/25/17.
 */
public class Request implements Serializable{
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    public Request (String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }
    public String getMethodName() { return methodName; }
    public Class<?>[] getParameterTypes() { return parameterTypes;}
    public Object[] getArguments() { return arguments;}

}
