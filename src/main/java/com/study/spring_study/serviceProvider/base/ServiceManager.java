package com.study.spring_study.serviceProvider.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供注册
 * DriverManager registerDriver getConnection
 */
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