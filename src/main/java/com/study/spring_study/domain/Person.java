package com.study.spring_study.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Person {

    public Person(String name) {
        this.name = name;
    }


    private String name;

    private Integer age=123;
}
