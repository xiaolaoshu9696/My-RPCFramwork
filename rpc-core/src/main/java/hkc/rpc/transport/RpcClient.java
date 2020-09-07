package hkc.rpc.transport;


import hkc.rpc.entity.RpcRequest;
import hkc.rpc.serializer.CommonSerializer;

/**
 * @author hkc
 * @date 2020/8/29 12:57
 *
 * 客户端类通用接口
 *
 */
public interface RpcClient {
    Object sendRequest(RpcRequest  rpcRequest);
    void setSerializer(CommonSerializer serializer);

}
