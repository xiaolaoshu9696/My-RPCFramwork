package hkc.test;

import hkc.rpc.api.ADDService;
import hkc.rpc.api.HelloService;
import hkc.rpc.serializer.KryoSerializer;
import hkc.rpc.serializer.ProtobufSerializer;
import hkc.rpc.transport.netty.server.NettyServer;

import hkc.rpc.registry.ServiceRegistry;


/**
 * @author hkc
 * @date 2020/8/29 16:53
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ADDService addService = new ADDServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9998);
        server.setSerializer(new KryoSerializer());
        server.publishService(helloService, HelloService.class);
        server.publishService(addService, ADDService.class);
        server.start();

    }

}
