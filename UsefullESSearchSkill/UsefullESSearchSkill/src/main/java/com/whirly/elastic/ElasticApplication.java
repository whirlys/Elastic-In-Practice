package com.whirly.elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElasticApplication {

    public static void main(String[] args) {
        // 允许参数中带有特殊字符，方便正则表达式参数的传递
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","|[]*()");
        SpringApplication.run(ElasticApplication.class, args);
    }
}
