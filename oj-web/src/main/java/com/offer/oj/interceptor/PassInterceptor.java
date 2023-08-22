package com.offer.oj.interceptor;

import com.offer.oj.domain.dto.UserIdentityDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.interceptor.common.Utils;
import com.offer.oj.common.service.CacheService;
import com.offer.oj.platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.sasl.AuthenticationException;
import java.util.Objects;

public class PassInterceptor implements HandlerInterceptor {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws AuthenticationException {
        Integer userId = (Integer) cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(Utils.getUserId(request));
        UserIdentityDTO userIdentityDTO;
        try {
            userIdentityDTO = userService.getUserIdentity(userId);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
        request.setAttribute("UserIdentityDTO", Objects.requireNonNullElseGet(userIdentityDTO, UserIdentityDTO::new));
        return true;
    }
}
