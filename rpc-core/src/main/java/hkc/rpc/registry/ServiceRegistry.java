package hkc.rpc.registry;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/28 14:57
 * 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);



}
