package hkc.rpc.server;

import com.sun.xml.internal.ws.encoding.MtomCodec;
import hkc.rpc.entity.RpcRequest;
import hkc.rpc.entity.RpcResponse;
import hkc.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 16:14
 * 进行过程调用的处理器
 */


public class RequestHandler{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务：{} 成功调用方法：{}",rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (IllegalAccessException |InvocationTargetException  e) {
            logger.error("调用或发送时有错误发送",e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());

    }

}
