package hkc.test;

import hkc.rpc.RpcClient;
import hkc.rpc.api.ADDService;
import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;
import hkc.rpc.RpcClientProxy;
import hkc.rpc.socket.client.SocketClient;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 16:43
 * 测试用消费者（客户端）
 */
public class TestClient {

    public static void main(String[] args){

        RpcClient client = new SocketClient("127.0.0.1",9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        ADDService addService = proxy.getProxy(ADDService.class);
        HelloObject object = new HelloObject(11, "This is a message");
        String res = helloService.hello(object);
        int add = addService.add(2,3);
        System.out.println(res);
        System.out.println(add);
    }
}
