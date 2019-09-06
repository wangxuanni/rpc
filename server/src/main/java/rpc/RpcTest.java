package rpc;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: wangxuanni
 * @create: 2019-09-06 11:57
 **/

public class RpcTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RpcExporter.exporter("localhost", 7890);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        RpcImporter<EchoService> proxyImporter = new RpcImporter<>();
        EchoService echoService = proxyImporter.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 7890));
        System.out.println(echoService.echo("rpc exe->"));
    }

}
