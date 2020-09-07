package hkc.rpc.hook;

import hkc.rpc.utils.NacosUtil;
import hkc.rpc.utils.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hkc
 * @date 2020/9/5 20:20
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
