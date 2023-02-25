package com.study.spring_study.handle;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;

/**
 * httpRequestHandle 没有返回值，只能处理简单的http请求
 */
public class MyHttpRequestHandle implements HttpRequestHandler {


    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().println("测试httpRequestHandle");
    }
}
