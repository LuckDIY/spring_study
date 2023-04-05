package com.study.spring_study.serviceProvider;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring_study.serviceProvider.base.Service;
import com.study.spring_study.serviceProvider.base.ServiceManager;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Test {
 
    public static void main(String[] args) throws ClassNotFoundException, JsonProcessingException {
       /* Class.forName("com.study.spring_study.serviceProvider.FirstServiceProvider");
        Service service = ServiceManager.getService("firstServiceProvider");
        service.print();*/

        User user = new User();
        user.setName("aaa");
        user.setDate(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(user);
        System.out.println(s);

    }

    @Data
    public static class User{

        private String name;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GM-8")
        private Date date;
    }
 
}