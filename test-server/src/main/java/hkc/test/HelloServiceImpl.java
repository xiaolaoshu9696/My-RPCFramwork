package hkc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hkc.rpc.api.HelloObject;
import hkc.rpc.api.HelloService;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 14:51
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "这是调用的返回值， id = " + object.getId();
    }
}
