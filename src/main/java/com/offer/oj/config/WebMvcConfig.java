package com.offer.oj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 设置静态资源的路径和 MIME 类型
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

//    @Override
//    public void configureContentNegotiation(ContentNegotiationConfigurer configurer){
//        configurer.favorParameter(true)
//                .ignoreAcceptHeader(true)
//                .defaultContentType(MediaType.APPLICATION_JSON)
//                .mediaType("js", MediaType.valueOf("application/javascript"))
//                .mediaType("css", MediaType.valueOf("text/css"));
//    }
}
