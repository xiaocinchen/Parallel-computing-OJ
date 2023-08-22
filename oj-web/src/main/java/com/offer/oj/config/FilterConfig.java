package com.offer.oj.config;

import com.offer.oj.filter.IpRateLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private IpRateLimitFilter ipRateLimitFilter;

    @Bean
    public FilterRegistrationBean<IpRateLimitFilter> loggingFilter(){
        FilterRegistrationBean<IpRateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(ipRateLimitFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
