package rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @description:客户端
 * @author: wangxuanni
 * @create: 2019-09-06 11:56
 **/

public class RpcImporter<S> {


    /**
     * 通过jdk动态代理，传入类加载器、类、代理类（匿名实现）。返回一个代理实例
     * 目的是把本地接口转换成JDK代理对象
     * @param serviceClass
     * @param addr
     * @return
     */
    public S importer(final Class<?> serviceClass, final InetSocketAddress addr) {

        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass.getInterfaces()[0]},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        SocketChannel socketChannel=null;
                        try {
                             socketChannel = SocketChannel.open();
                            socketChannel.socket().connect(addr);

                            Model model = new Model(serviceClass.getName(), method.getName(), method.getParameterTypes(), args);
                            String modelStr = JSONObject.toJSONString(model);

                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            socketChannel.write(Charset.forName("UTF-8").encode(modelStr));

                            String objectStr = "";
                            while (socketChannel.read(buffer) > 0) {
                                buffer.flip();
                                objectStr += Charset.forName("UTF-8").decode(buffer);
                            }
                            Object object = JSON.parseObject(objectStr, Object.class);

                            socketChannel.close();
                            return object;
                        } finally {

                        }

                    }

                });
    }

}

