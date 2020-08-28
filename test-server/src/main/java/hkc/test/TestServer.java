package hkc.test;

import hkc.rpc.api.ADDService;
import hkc.rpc.api.HelloService;
import hkc.rpc.registry.DefaultServiceRegistry;
import hkc.rpc.registry.ServiceRegistry;
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
        ADDService addService = new ADDServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(addService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
