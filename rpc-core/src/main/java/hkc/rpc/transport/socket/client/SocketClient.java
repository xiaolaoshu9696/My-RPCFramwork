package hkc.rpc.transport.socket.client;

import hkc.rpc.registry.NacosServiceDiscovery;
import hkc.rpc.registry.NacosServiceRegistry;
import hkc.rpc.registry.ServiceDiscovery;
import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.serializer.CommonSerializer;
import hkc.rpc.transport.RpcClient;
import hkc.rpc.entity.RpcRequest;
import hkc.rpc.entity.RpcResponse;
import hkc.rpc.enumeration.ResponseCode;
import hkc.rpc.enumeration.RpcError;
import hkc.rpc.exception.RpcException;
import hkc.rpc.transport.socket.utils.ObjectReader;
import hkc.rpc.transport.socket.utils.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 15:44
 * 远程方法调用的消费者（客户端）Socket 方式
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;

    public SocketClient(){
        this.serviceDiscovery = new NacosServiceDiscovery(null);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(rpcRequest);
//            objectOutputStream.flush();
//            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();

        }catch (IOException e){
            logger.error("调用时有错误发生", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }


}
