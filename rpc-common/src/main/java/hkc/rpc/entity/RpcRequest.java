package hkc.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hkc
 * @version 1.0
 * @date 2020/8/27 14:57
 * 消费者向提供者发送的请求对象
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 代调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */

    private Class<?>[] paramTypes;

}
