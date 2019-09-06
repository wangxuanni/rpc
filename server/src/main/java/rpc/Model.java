package rpc;

import java.io.Serializable;

/**
 * @description:
 * @author: wangxuanni
 * @create: 2019-09-06 13:16
 **/

public class Model implements Serializable {
    String className;
    String methodName;
    Class<?>[] parameterTypes;
    Object[] args;

    public Model() {
    }

    public Model(String className, String methodName, Class<?>[] parameterTypes, Object[] args) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
