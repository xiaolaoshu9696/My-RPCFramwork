package hkc.rpc.transport.netty.client;

import hkc.rpc.enumeration.RpcError;
import hkc.rpc.exception.RpcException;
import hkc.rpc.registry.NacosServiceDiscovery;
import hkc.rpc.registry.NacosServiceRegistry;
import hkc.rpc.registry.ServiceDiscovery;
import hkc.rpc.registry.ServiceRegistry;
import hkc.rpc.serializer.CommonSerializer;
import hkc.rpc.transport.RpcClient;
import hkc.rpc.codec.CommonDecoder;
import hkc.rpc.codec.CommonEncoder;
import hkc.rpc.entity.RpcRequest;
import hkc.rpc.entity.RpcResponse;
import hkc.rpc.serializer.KryoSerializer;
import hkc.rpc.utils.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hkc
 * @date 2020/8/29 16:46
 * NIO方式消费侧客户端类
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;

    static {
        //客户端引导类
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient() {
        this.serviceDiscovery = new NacosServiceDiscovery(null);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            //找到服务地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //获取Channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                //客户端发送数据
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                //等待连接关闭
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
