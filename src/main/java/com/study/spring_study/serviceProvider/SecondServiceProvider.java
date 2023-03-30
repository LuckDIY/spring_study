package com.study.spring_study.serviceProvider;

import com.study.spring_study.serviceProvider.base.Service;
import com.study.spring_study.serviceProvider.base.ServiceManager;
import com.study.spring_study.serviceProvider.base.ServiceProvider;

public class SecondServiceProvider implements ServiceProvider {
 
    static{
        ServiceManager.registerProvider("secondServiceProvider", new SecondServiceProvider());
    }  
 
    @Override
    public Service getService() {
        return new SecondService();
    }
 
}