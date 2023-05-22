package com.study.spring_study;

import com.study.spring_study.domain.Person;
import com.study.spring_study.serviceProvider.FirstService;
import lombok.extern.slf4j.Slf4j;
import com.study.spring_study.serviceProvider.SecondService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@SpringBootTest
class SpringStudyApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    void testBeanDefinitionRegistry() {
        //BeanDefinitionRegistry的差异
        BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        //需要显式注册,无需@Component注解
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        reader.registerBean(FirstService.class);

        BeanDefinition firstService = reader.getRegistry().getBeanDefinition("firstService");
        System.out.println(firstService);

        //内部已集成reader和scanner
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.scan("com.study.spring_study");

    }

    @Test
    void testBeanDefinitionReader() {

        BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        //需要显式注册,无需@Component注解
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanDefinitionRegistry);
        reader.registerBean(FirstService.class);

        BeanDefinition firstService = reader.getRegistry().getBeanDefinition("firstService");
        System.out.println(firstService);

    }

    @Test
    void testBeanDefinitionScanner() {

        BeanDefinitionRegistry beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();

        //扫描并注册加@Component注解的
        ClassPathBeanDefinitionScanner reader = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        reader.scan("com.study.spring_study");

        BeanDefinition firstService = reader.getRegistry().getBeanDefinition("demoController");
        System.out.println(firstService);

    }

    /**
     * 测试内省者api
     */
    @Test
    void testIntrospector() throws IntrospectionException, InvocationTargetException, IllegalAccessException {

        Person person = new Person("wang");

        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class);
        BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            log.info("类型:{}",propertyType.getName());
        }

        PropertyDescriptor age = new PropertyDescriptor("age", Person.class);
        age.getWriteMethod().invoke(person,15);

        PropertyDescriptor name = new PropertyDescriptor("name", Person.class);
        Object invoke = name.getReadMethod().invoke(person);
        log.info("name:{}",invoke);

        log.info("person:{}",person);

    }

    @Test
    void testConversionService(){

        ConversionService sharedInstance = DefaultConversionService.getSharedInstance();
        Integer convert = sharedInstance.convert("123", Integer.class);
        log.info("打印转化:{}",convert);
    }

    private List<String> list;

    @Test
    void testResolvableType() throws NoSuchFieldException {

        ResolvableType param = ResolvableType.forField(this.getClass().getDeclaredField("list"));

        Type type = param.getType();
        log.info("打印:{}",type.getTypeName());
        log.info("打印:{}",param.getGeneric(0).getType().getTypeName());
    }



    @Test
    void testEventMulticaster() {

        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
        simpleApplicationEventMulticaster.addApplicationListener(event -> System.out.println(event.getSource()+"测试listener"));
        simpleApplicationEventMulticaster.multicastEvent(new ApplicationEvent(new SecondService()) {
            @Override
            public Object getSource() {
                return super.getSource();
            }
        });

    }


    @Test
    void testBeanFactory() {

        DefaultListableBeanFactory beanfactory = new DefaultListableBeanFactory();
        beanfactory.registerSingleton("person",new Person("abc"));

        Object person = beanfactory.getBean("person");
        log.info("person:{}",person);

    }

}
