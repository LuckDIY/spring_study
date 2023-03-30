package com.study.spring_study.serviceProvider;

import com.study.spring_study.serviceProvider.base.Service;

/**
 * 服务接口实现类
 */
public class SecondService implements Service {
 
    @Override
    public void print() {
        System.out.println("第二个服务");
    }
 
}