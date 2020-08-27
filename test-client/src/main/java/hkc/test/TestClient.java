package hkc.test;

import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;
import hkc.rpc.client.RpcClient;
import hkc.rpc.client.RpcClientProxy;

import java.awt.*;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 16:43
 * 测试用消费者（客户端）
 */
public class TestClient {

    public static void main(String[] args){

        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(11, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
