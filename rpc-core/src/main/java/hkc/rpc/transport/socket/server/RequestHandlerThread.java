package hkc.rpc.transport.socket.server;

import hkc.rpc.entity.RpcRequest;
import hkc.rpc.entity.RpcResponse;
import hkc.rpc.handler.RequestHandler;
import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.serializer.CommonSerializer;
import hkc.rpc.transport.socket.utils.ObjectReader;
import hkc.rpc.transport.socket.utils.ObjectWriter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @author hkc
 * @date 2020/8/28 17:19
 * 处理RpcRequest的工作线程
 */

@AllArgsConstructor
public class RequestHandlerThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
