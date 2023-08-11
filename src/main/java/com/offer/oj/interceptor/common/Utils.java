package com.offer.oj.interceptor.common;

import com.offer.oj.domain.enums.CacheEnum;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Utils {
    public static String getUserId(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(a -> Objects.equals(a.getName(), "TOKEN"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
