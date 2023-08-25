package com.offer.oj;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableMethodCache(basePackages = "com.offer.oj")
@ImportResource("classpath:oj-platform-provider.xml")
//@MapperScan(basePackages = "com.offer.oj.dao.mapper")
@ServletComponentScan
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class OjApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjApplication.class, args);
    }
}
