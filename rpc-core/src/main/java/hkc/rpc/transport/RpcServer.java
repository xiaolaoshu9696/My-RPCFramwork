package hkc.rpc.transport;

import hkc.rpc.serializer.CommonSerializer;

/**
 * @author hkc
 * @date 2020/8/29 13:18
 */
public interface RpcServer {
    void start();

    void setSerializer(CommonSerializer serializer);

    <T> void publishService(Object service, Class<T> serviceClass);
}
