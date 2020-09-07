package hkc.rpc.transport.socket.server;

import hkc.rpc.enumeration.RpcError;
import hkc.rpc.exception.RpcException;
import hkc.rpc.provider.ServiceProvider;
import hkc.rpc.provider.ServiceProviderImpl;
import hkc.rpc.registry.NacosServiceRegistry;
import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.handler.RequestHandler;
import hkc.rpc.serializer.CommonSerializer;
import hkc.rpc.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    private final String host;
    private final int port;
    private CommonSerializer serializer;

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
    private final ServiceProvider serviceProvider;

    private RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动……");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }



}
