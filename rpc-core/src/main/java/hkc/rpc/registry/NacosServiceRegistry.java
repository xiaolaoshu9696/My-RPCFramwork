package hkc.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import hkc.rpc.enumeration.RpcError;
import hkc.rpc.exception.RpcException;
import hkc.rpc.utils.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author hkc
 * @date 2020/9/2 16:17
 * Nacos服务注册中心
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);


    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }


}
