package com.offer.oj.config;

import com.offer.oj.interceptor.AuthorizationInterceptor;
import com.offer.oj.interceptor.BaseInterceptor;
import com.offer.oj.interceptor.PassInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(baseInterceptor())
                .addPathPatterns("/v1/**")
                .excludePathPatterns("/v1/login/**")
                .excludePathPatterns("/v1/register/**")
                .excludePathPatterns("/v1/kaptcha/**")
                .excludePathPatterns("/v1/password/**")
                .excludePathPatterns("/v1/question/search")
                .order(1);

        registry.addInterceptor(authorizationInterceptor())
                .addPathPatterns("/v1/question/**")
                .excludePathPatterns("/v1/question/search")
                .excludePathPatterns("/v1/kaptcha/**")
                .addPathPatterns("/v1/code/propose")
                .order(2);

        registry.addInterceptor(passInterceptor())
                .addPathPatterns("/v1/question/search")
                .order(3);
    }

    @Bean
    public AuthorizationInterceptor authorizationInterceptor(){
        return new AuthorizationInterceptor();
    }

    @Bean
    public BaseInterceptor baseInterceptor(){
        return new BaseInterceptor();
    }

    @Bean
    public PassInterceptor passInterceptor(){
        return new PassInterceptor();
    }
}
