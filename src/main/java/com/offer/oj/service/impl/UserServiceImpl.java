package com.offer.oj.service.impl;


import com.offer.oj.dao.Result;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.OjUser;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import com.offer.oj.util.LoginCacheUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    @Autowired
    private OjUserMapper ojUserMapper;

    @Override
    public Result register(UserDTO userDTO, boolean isStudent) {
        Result result = new Result<>();
        String message = "";
        if (isUserDTOEmpty(userDTO)) {
            message = "Incomplete user information";
            log.error(message);
        } else if (userDTO.getUsername().length() > 20 || userDTO.getUsername().length() < 6) {
            message = "The username should be between 6 and 20 characters! " + userDTO.getUsername();
            log.error(message);
        } else if (!Pattern.compile(EMAIL_REGEX).matcher(userDTO.getEmail()).matches()) {
            message = "Email format error! " + userDTO.getEmail();
            log.error(message);
        } else if (userDTO.getPassword().length() > 20 || userDTO.getPassword().length() < 8 || !userDTO.getPassword().matches("[a-zA-Z0-9]+")) {
            message = "Password should be composed of 8 to 20 characters of numbers or English!";
            log.error(message);
        } else if (userDTO.getFirstName().length() > 12) {
            message = "FirstName cannot exceed 12 characters! " + userDTO.getFirstName();
            log.error(message);
        } else if (userDTO.getLastName().length() > 12) {
            message = "LastName cannot exceed 12 characters! " + userDTO.getLastName();
            log.error(message);
        } else if (Stream.of("male", "female", "secret").noneMatch(userDTO.getGender()::equals)) {
            message = "Unknown gender! " + userDTO.getGender();
            log.error(message);
        } else if (ojUserMapper.selectByUsername(userDTO.getUsername()) != null) {
            message = "Username already exists! " + userDTO.getUsername();
            log.error(message);
        } else if (ojUserMapper.selectByEmail(userDTO.getEmail()) != null) {
            message = "Email already exists! " + userDTO.getEmail();
            log.error(message);
        }
        if (!message.isEmpty()) {
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
        if (isStudent) {
            userDTO.setRole("student");
        } else {
            userDTO.setRole("teacher");
        }
        userDTO.setPassword(Encryption.hashPassword(userDTO.getPassword()));
        OjUser ojUser = new OjUser();
        BeanUtils.copyProperties(userDTO, ojUser);
        try {
            ojUserMapper.insertSelective(ojUser);
            message = "用户注册成功";
            log.info(message + "{}", userDTO.getUsername());
        } catch (Exception e) {
            message = "用户注册失败";
            log.error(message + "{}", userDTO.getUsername(), e);
            throw new RuntimeException("用户表插入异常");
        }
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    @Override
    public boolean isUserDTOEmpty(UserDTO userDTO) {
        return StringUtils.isEmpty(userDTO.getUsername())
                || StringUtils.isEmpty(userDTO.getPassword())
                || StringUtils.isEmpty(userDTO.getFirstName())
                || StringUtils.isEmpty(userDTO.getLastName())
                || StringUtils.isEmpty(userDTO.getEmail())
                || userDTO.getGender() == null;
    }

    @Override
    public Result login(LoginDTO loginDTO, HttpServletResponse response) {

        // Incomplete input
        if(null==loginDTO.getUsername()||null==loginDTO.getPassword()){
            return new Result("Incomplete Login Information!");
        }

        // Incorrect info
        OjUser ojUser=ojUserMapper.selectByUsername(loginDTO.getUsername());
        if(null==ojUser){
            return new Result("Incorrect Username or Password!");
        }else if(!Encryption.checkPassword(loginDTO.getPassword(),ojUser.getPassword())){
            return new Result("Incorrect Username or Password!");
        }

        // Correct info & Login
        else{
            // SSO
            String userId=ojUser.getId().toString();                     // Get UserId
            String token= UUID.randomUUID().toString();                  // Get Token
            Collection<String> values= LoginCacheUtil.loginUser.values(); // Save Token
            values.remove(userId);
            LoginCacheUtil.loginUser.put(token,userId);
            Cookie cookie=new Cookie("TOKEN",token);                     // Set Cookie
            cookie.setDomain("127.0.0.1");
            cookie.setPath("/");
            response.addCookie(cookie);
            // Return result
            Result result=new Result();
            result.setSuccess(true);
            result.setCode(0);
            result.setMessage("Login successfully!");
            return result;
        }
    }
    @Override
    public boolean isLogin(Cookie cookie) {
        return null!=cookie && LoginCacheUtil.loginUser.containsKey(cookie.getValue());
    }

    @Override
    public Result logout(Cookie cookie) {
        if(null!=cookie){
            LoginCacheUtil.loginUser.remove(cookie.getValue());
        }
        Result result=new Result();
        result.setSuccess(true);
        result.setCode(0);
        result.setMessage("Logout Successfully!");
        return result;
    }


    @Override
    public Result userInfo(Cookie cookie) {
        if(null!=cookie){
            String userId=LoginCacheUtil.loginUser.get(cookie.getValue());

        }
        return null;
    }
}
