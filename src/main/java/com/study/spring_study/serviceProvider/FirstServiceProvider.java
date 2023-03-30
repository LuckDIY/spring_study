package com.study.spring_study.serviceProvider;

import com.study.spring_study.serviceProvider.base.Service;
import com.study.spring_study.serviceProvider.base.ServiceManager;
import com.study.spring_study.serviceProvider.base.ServiceProvider;

public class FirstServiceProvider implements ServiceProvider {
 
    static{
        ServiceManager.registerProvider("firstServiceProvider", new FirstServiceProvider());
    } 
 
    @Override
    public Service getService() {
        return new FirstService();
    }
 
}