package hkc.rpc.socket.server;

import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.RequestHandler;
import hkc.rpc.RequestHandlerThread;
import hkc.rpc.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 15:55
 * 远程方法调用的提供者（服务端）
 */
public class SocketServer implements RpcServer {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    //线程池参数
    private final ThreadPoolExecutor threadPool;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY =100;

    //服务注册表
    private final ServiceRegistry serviceRegistry;

    private RequestHandler requestHandler = new RequestHandler();

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动");
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                logger.info("消费者连接！ ip为{}：{}", socket.getInetAddress(),socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("连接时有错误发生：", e);
        }
    }

}
