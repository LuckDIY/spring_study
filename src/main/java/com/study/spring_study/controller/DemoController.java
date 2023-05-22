package com.study.spring_study.controller;

import com.study.spring_study.domain.Person;
import org.dromara.sms4j.comm.enumerate.SupplierType;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
public class DemoController implements EnvironmentAware {


    /**
     * https://dysmsapi.aliyuncs.com/?
     * Signature=Y4wdvjeFPw9ayvVmnV4Vw/ixSEQ=
     * &SignatureVersion=1.0
     * &Action=SendSms
     * &Format=JSON
     * &SignatureNonce=cc3f5d34-0b94-40ec-83e8-c589f331ac6b
     * &Version=2017-05-25
     * &AccessKeyId=LTAI5tAt3Q2vhuUnMT4W8xiG
     * &SignatureMethod=HMAC-SHA1
     * &RegionId=cn-hangzhou
     * &Timestamp=2023-05-06T04%3A17%3A39Z
     * @return
     */
    @GetMapping("ok")
    public String ok() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("code","12345");
        SmsFactory.createSmsBlend(SupplierType.ALIBABA)
                .sendMessage("13253691438","SMS_171746001", map);

        return "ok";
    }

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }



}
