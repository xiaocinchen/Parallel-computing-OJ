package com.offer.oj;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodCache(basePackages = "com.offer.oj")
@MapperScan(basePackages = "com.offer.oj.dao.mapper")
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class OjApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjApplication.class, args);
    }
}
