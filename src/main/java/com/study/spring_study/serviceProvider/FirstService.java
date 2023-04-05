package com.study.spring_study.serviceProvider;

import com.study.spring_study.serviceProvider.base.Service;
import org.springframework.stereotype.Component;

/**
 * 服务接口实现类
 */
public class FirstService implements Service {
 
    @Override
    public void print() {
        System.out.println("第一个服务");
    }
 
}