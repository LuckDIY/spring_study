package com.study.spring_study.handleMapping;

import com.study.spring_study.handle.MyHttpRequestHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Collections;

@Configuration
public class HandleMappingConfig {



    @Bean
    public SimpleUrlHandlerMapping testSimpleUrlHandlerMapping(){
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

        //设置高优先级，有/**的handle 排序是 2147483646，需要大于这个值
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(Collections.singletonMap("testSimpleHandle",new MyHttpRequestHandle()));

        return mapping;
    }
}
