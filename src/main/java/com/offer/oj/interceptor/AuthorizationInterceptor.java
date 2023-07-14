package com.offer.oj.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String token = Optional.ofNullable(request.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(a -> Objects.equals(a.getName(), "TOKEN"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        if (token == null){

        }
        return true;
    }

}
