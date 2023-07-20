package com.offer.oj.filter;

import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
public class IpRateLimitFilter implements Filter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_VISIT = 1000;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ip = servletRequest.getRemoteAddr();
        String key = "ip:" + ip + ":" + LocalDate.now();
        Long ipCurrentSize = redisTemplate.opsForHyperLogLog().add(key, ip);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        if (ipCurrentSize > MAX_VISIT) {
            servletResponse.getWriter().write("Exceed the maximum visits!");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
