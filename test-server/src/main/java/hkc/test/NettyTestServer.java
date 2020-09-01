package hkc.test;

import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;
import hkc.rpc.netty.server.NettyServer;
import hkc.rpc.registry.DefaultServiceRegistry;
import hkc.rpc.registry.ServiceRegistry;


/**
 * @author hkc
 * @date 2020/8/29 16:53
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
