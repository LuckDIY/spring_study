package com.study.spring_study.serviceProvider;

import com.study.spring_study.serviceProvider.base.Service;
import com.study.spring_study.serviceProvider.base.ServiceManager;

public class Test {
 
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.study.spring_study.serviceProvider.FirstServiceProvider");
        Service service = ServiceManager.getService("firstServiceProvider");
        service.print();
    }
 
}