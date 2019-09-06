package rpc;

/**
 * @description:
 * @author: wangxuanni
 * @create: 2019-09-06 11:55
 **/

public class EchoServiceImpl  implements EchoService {
    @Override
    public String echo(String ping) {
        return ping != null ? ping+"-->神奇宝贝闪亮登场" : "召唤失败";
    }
}
