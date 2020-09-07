package hkc.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author hkc
 * @date 2020/9/7 19:30
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index>= instances.size()){
            index%=instances.size();
        }
        return instances.get(index++);
    }
}
