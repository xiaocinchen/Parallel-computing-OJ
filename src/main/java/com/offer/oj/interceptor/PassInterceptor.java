package com.offer.oj.interceptor;

import com.offer.oj.domain.dto.UserIdentityDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.interceptor.common.Utils;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.sasl.AuthenticationException;

public class PassInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws AuthenticationException {
        Integer userId = (Integer) cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(Utils.getUserId(request));
        try {
            request.setAttribute("UserIdentityDTO", userService.getUserIdentity(userId));
        } catch (Exception e){
            throw new AuthenticationException(e.getMessage());
        }
        return true;
    }
}
