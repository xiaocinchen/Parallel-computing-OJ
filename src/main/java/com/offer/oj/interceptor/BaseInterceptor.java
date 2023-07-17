package com.offer.oj.interceptor;

import com.offer.oj.domain.dto.UserIdentityDTO;
import com.offer.oj.domain.enums.CacheEnum;
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

public class BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        Integer userId = this.getUserId(request);
        if (userId != null) {
            try {
                UserIdentityDTO userIdentityDTO;
                if ((userIdentityDTO = userService.getUserIdentity(userId)) != null) {
                    request.setAttribute("UserIdentityDTO", userIdentityDTO);
                    return true;
                }
            } catch (Exception e) {
                throw new AuthenticationException("User authority exception.", e);
            }
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Permission denied.");
        return false;
    }

    private Integer getUserId(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(a -> Objects.equals(a.getName(), "TOKEN"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        return (Integer) cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(token);
    }
}
