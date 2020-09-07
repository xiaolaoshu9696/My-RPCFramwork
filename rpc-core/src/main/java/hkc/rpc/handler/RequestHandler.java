package hkc.rpc.handler;

import hkc.rpc.entity.RpcRequest;
import hkc.rpc.entity.RpcResponse;
import hkc.rpc.enumeration.ResponseCode;
import hkc.rpc.provider.ServiceProvider;
import hkc.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 16:14
 * 进行过程调用的处理器
 */


public class RequestHandler{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest){
        Object result = null;
        //获取调用的方法
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        try{
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务：{} 成功调用方法：{}",rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (IllegalAccessException |InvocationTargetException  e) {
            logger.error("调用或发送时有错误发送",e);
        }
        return result;
    }

    //反射调用方法返回结果
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD,rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());

    }

}
