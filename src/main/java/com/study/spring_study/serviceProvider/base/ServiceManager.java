package com.study.spring_study.serviceProvider.base;

import com.study.spring_study.serviceProvider.base.Service;
import com.study.spring_study.serviceProvider.base.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
 
    private static final Map<String, ServiceProvider> providers = new ConcurrentHashMap<String, ServiceProvider>();
 
    public static void registerProvider(String name, ServiceProvider p) {
        providers.put(name, p);
    }
 
    public static Service getService(String name) {
        ServiceProvider p = providers.get(name);
           if (p == null) {
               throw new IllegalArgumentException(
                       "No ServiceProvider registered with name:" + name);
           }
           return p.getService();
        }
}