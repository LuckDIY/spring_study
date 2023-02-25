package com.study.spring_study.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("ok")
    public String ok(){
        return "ok";
    }

}
