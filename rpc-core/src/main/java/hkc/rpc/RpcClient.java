package hkc.rpc;


import hkc.rpc.entity.RpcRequest;

/**
 * @author hkc
 * @date 2020/8/29 12:57
 */
public interface RpcClient {
    Object sendRequest(RpcRequest  rpcRequest);
}
