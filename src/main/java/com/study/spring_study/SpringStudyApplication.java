package com.study.spring_study;

import com.study.spring_study.domain.Person;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringStudyApplication implements BeanFactoryPostProcessor {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringStudyApplication.class, args);
        Object person = run.getBean("person");

        System.out.println(person);
    }



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition person = beanFactory.getBeanDefinition("person");
        person.getConstructorArgumentValues().addGenericArgumentValue("搭建好");

        MutablePropertyValues propertyValues = person.getPropertyValues();
        propertyValues.addPropertyValue("age",10);
    }
}
