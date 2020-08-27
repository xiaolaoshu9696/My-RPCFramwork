package hkc.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 15:55
 * 远程方法调用的提供者（服务端）
 */
public class RpcServer {

    private final ThreadPoolExecutor threadPool;
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    public RpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void register(Object service, int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器正在启动");
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                logger.info("客户端连接！ ip为：{}", socket.getInetAddress());
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

}
