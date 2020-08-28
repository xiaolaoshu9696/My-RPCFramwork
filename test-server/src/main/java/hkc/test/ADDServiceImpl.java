package hkc.test;

import hkc.rpc.api.ADDService;

/**
 * @author hkc
 * @date 2020/8/28 19:23
 */
public class ADDServiceImpl implements ADDService {

    @Override
    public int add(int a, int b) {
        return a+b;
    }
}
