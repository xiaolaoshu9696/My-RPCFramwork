package hkc.rpc.provider;

/**
 * @author hkc
 * @date 2020/9/2 15:56
 * 保存和提供服务实例对象
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service);

    Object getServiceProvider(String serviceName);
}
