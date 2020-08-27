package hkc.rpc.client;

import hkc.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 15:44
 * 远程方法调用的消费者（客户端）
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object setRequest(RpcRequest rpcRequest, String host, int port) {
        try(Socket socket = new Socket(host, port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();

        }catch (IOException | ClassNotFoundException e){
            logger.error("调用时有错误发生", e);
            return null;
        }
    }

}
