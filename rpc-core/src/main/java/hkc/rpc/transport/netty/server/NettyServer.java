package hkc.rpc.transport.netty.server;

import hkc.rpc.enumeration.RpcError;
import hkc.rpc.exception.RpcException;
import hkc.rpc.hook.ShutdownHook;
import hkc.rpc.provider.ServiceProvider;
import hkc.rpc.provider.ServiceProviderImpl;
import hkc.rpc.registry.NacosServiceRegistry;
import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.serializer.CommonSerializer;
import hkc.rpc.transport.RpcServer;
import hkc.rpc.codec.CommonDecoder;
import hkc.rpc.codec.CommonEncoder;
import hkc.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author hkc
 * @date 2020/8/29 13:42
 */
public class NettyServer implements RpcServer {

    private final String host;
    private final int port;

    //服务注册表和提供
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    //序列化
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));

    }

    @Override
    public void start() {
        //添加钩子，自动注销服务
        ShutdownHook.getShutdownHook().addClearAllHook();

        // bossGroup ⽤于接收连接，workerGroup ⽤于具体的处理 默认构造函数会起2*cpu核心线程数
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //创建一个服务端启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //给引导类配置两大线程组，确定了线程模型
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口
            ChannelFuture future = serverBootstrap.bind(host, port).sync();

            //等待连接关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            //优雅的关闭相关线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
