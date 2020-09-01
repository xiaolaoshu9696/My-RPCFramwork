package hkc.test;

import hkc.rpc.RpcClient;
import hkc.rpc.RpcClientProxy;
import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;
import hkc.rpc.netty.client.NettyClient;

/**
 * @author hkc
 * @date 2020/8/29 16:51
 * 测试用Netty消费者
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}
