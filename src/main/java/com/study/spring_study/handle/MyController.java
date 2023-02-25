package com.study.spring_study.handle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.Collections;
import java.util.HashMap;


/**
 * bean name使用/开口 会注册 BeanNameUrlHandleMapping
 * todo 取消测试，不想写index.html
 */
@Component("/MyController")
public class MyController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        return new ModelAndView("index", Collections.singletonMap("a","123"));
    }
}
