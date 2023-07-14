package com.offer.oj.interceptor;

import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.RoleEnum;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.sasl.AuthenticationException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String token = Optional.ofNullable(request.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(a -> Objects.equals(a.getName(), "TOKEN"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (token != null) {
            try {
                Integer userId = (Integer) cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(token);
                String username;
                if ((username = (userService.verifyRole(userId, RoleEnum.TEACHER.getValue()))) != null) {
                    request.setAttribute("username", username);
                    request.setAttribute("role", RoleEnum.TEACHER.getValue());
                    return true;
                }
            } catch (Exception e) {
                throw new AuthenticationException("User authority exception.");
            }
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

}
