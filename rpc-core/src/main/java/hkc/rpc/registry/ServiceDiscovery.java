package hkc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author hkc
 * @date 2020/9/7 19:42
 * 服务发现接口
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称获取服务实体
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
