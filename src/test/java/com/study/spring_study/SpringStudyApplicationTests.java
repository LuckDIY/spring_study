package com.study.spring_study;

import com.study.spring_study.serviceProvider.FirstService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

@SpringBootTest
class SpringStudyApplicationTests {

    @Test
    void contextLoads() {

        //BeanDefinitionRegistry beanDefinitionRegistry = new AnnotationConfigApplicationContext();
        BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        //需要显式注册
        //AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        //reader.registerBean(FirstService.class);

        //扫描并注册加@Component注解的
        ClassPathBeanDefinitionScanner reader = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        reader.scan("com.study.spring_study");

        BeanDefinition firstService = reader.getRegistry().getBeanDefinition("firstService");
        System.out.println(firstService);

    }

}
