package hkc.rpc.exception;

import hkc.rpc.enumeration.RpcError;

/**
 * @author hkc
 * @date 2020/8/28 15:10
 * RPC调用异常
 */
public class RpcException extends RuntimeException {
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ":" + detail);

    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
