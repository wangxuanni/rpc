package rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @description:服务端
 * @author: wangxuanni
 * @create: 2019-09-06 11:55
 **/

public class RpcExporter {
    private static final Logger logger = LoggerFactory.getLogger(RpcExporter.class);


    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void exporter(String hostName, int port) throws Exception {

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(hostName, port);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    it.remove();

                    if (selectionKey.isAcceptable()) {
                        serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        System.out.println("Connected: " + socketChannel.socket().getRemoteSocketAddress());
                    } else if (selectionKey.isReadable()) {

                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ExporterTask exporterTask=    new ExporterTask(socketChannel);
                        new Thread(exporterTask).start();
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    //如果不这样会有空指针异常，但不影响正常结果，推测是读key还没准备好就执行了ExporterTask
                    Thread.sleep(1000);


                }
            }
        }

    }

    private static class ExporterTask implements Runnable {
        SocketChannel client = null;

        public ExporterTask(SocketChannel client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                String modelStr = "";

                while (client.read(byteBuffer) > 0) {
                    byteBuffer.flip();
                    modelStr += Charset.forName("UTF-8").decode(byteBuffer);
                }

                System.out.println("exe:->modelStr");


                Model model = JSON.parseObject(modelStr, Model.class);

                Class<?> service = Class.forName(model.getClassName());

                //获取参数类型
                Class<?>[] parameterTypes = (Class<?>[])model.getParameterTypes();
                //获取返回值
                Object[] arguments = (Object[]) model.args;
                //反射拿到要调用的方法
                Method method = service.getMethod(model.getMethodName(), parameterTypes);
                //反射调用该方法拿到结果
                Object result = method.invoke(service.newInstance(), arguments);


                String resultStr = JSONObject.toJSONString(result);
                client.write(Charset.forName("UTF-8").encode(resultStr));


                byteBuffer.clear();
                client.close();



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}



