package hkc.test;

import hkc.rpc.api.HelloService;
import hkc.rpc.server.RpcServer;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 16:41
 * 测试用服务提供方（服务端）
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer  = new RpcServer();
        rpcServer.register(helloService,9000);
    }
}
