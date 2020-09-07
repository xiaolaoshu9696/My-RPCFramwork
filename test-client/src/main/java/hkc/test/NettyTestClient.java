package hkc.test;

import hkc.rpc.api.ADDService;
import hkc.rpc.serializer.KryoSerializer;
import hkc.rpc.serializer.ProtobufSerializer;
import hkc.rpc.transport.RpcClient;
import hkc.rpc.transport.RpcClientProxy;
import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;
import hkc.rpc.transport.netty.client.NettyClient;

/**
 * @author hkc
 * @date 2020/8/29 16:51
 * 测试用Netty消费者
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        ADDService addService = rpcClientProxy.getProxy(ADDService.class);
        String res = helloService.hello(object);
        System.out.println(res);
        System.out.println(addService.add(2,3));

    }
}
