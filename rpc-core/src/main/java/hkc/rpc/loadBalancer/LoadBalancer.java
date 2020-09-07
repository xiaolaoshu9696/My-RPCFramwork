package hkc.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author hkc
 * @date 2020/9/7 19:20
 */
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
