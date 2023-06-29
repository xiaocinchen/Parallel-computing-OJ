package com.offer.oj.service.impl;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OjUserMapper ojUserMapper;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, VerificationDTO> verificationDTOCache;

    private Cache<String, UserDTO> userDTOCache;

    @Override
    public Result<String> registerSendEmail(UserDTO userDTO) {
        Result<String> result = new Result<>();
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
        } else if (userMapper.selectByUsername(userDTO.getUsername()) != null) {
            message = "Username already exists! " + userDTO.getUsername();
            log.error(message);
        } else if (userMapper.selectByEmail(userDTO.getEmail()) != null) {
            message = "Email already exists! " + userDTO.getEmail();
            log.error(message);
        }
        if (message.isEmpty()) {
            try {
                Cache<String, UserDTO> usernameCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
                usernameCache.put(userDTO.getUsername(), userDTO);
                result.setSuccess(true);
                message = "验证邮件!";
                emailService.sendRegisterVerifyEmail(userDTO);
                log.info(message);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                result.setSuccess(false);
            }
        } else {
            result.setSuccess(false);
        }
        result.setMessage(message);
        result.setData("2023");
        return result;
    }

    @Override
    public Result registerVerifyEmail(VerificationDTO verification) {
        Result result = new Result();
        String message = "";
        if (Objects.isNull(verification) || ObjectUtils.isEmpty(verification.getUsername()) || ObjectUtils.isEmpty(verification.getCode()) || ObjectUtils.isEmpty(verification.getType())) {
            message = "验证码信息缺失";
            log.error(message);
        } else if (verification.getType().equals(EmailTypeEnum.REGISTER.getValue())) {
            log.info("开始验证邮件");
            userDTOCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
            UserDTO userDTO = userDTOCache.get(verification.getUsername());
            if (Objects.isNull(userDTO)) {
                message = "注册信息不存在或已过期!";
                log.error(message);
                result.setSimpleResult(false, message);
                return result;
            }
            verificationDTOCache = cacheManager.getCache(CacheEnum.REGISTER_CACHE.getValue());
            if (!Objects.isNull(verificationDTOCache.get(userDTO.getUsername())) && !Objects.isNull(verificationDTOCache.get(userDTO.getUsername()).getCode()) && verificationDTOCache.get(userDTO.getUsername()).getCode().equals(verification.getCode())) {
                message = "邮件验证成功!";
                log.info(message);
                result = register(userDTO);
                verificationDTOCache.remove(userDTO.getUsername());
            } else {
                message = "验证码错误或已过期，邮件验证失败!";
                result.setSimpleResult(false, message);
                log.error(message + "{}", verificationDTOCache.get(userDTO.getUsername()));
            }
        }
        return result;
    }

    public Result register(UserDTO userDTO) {
        Result result = new Result();
        String message = "";
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
        result.setSimpleResult(true, message);
        return result;
    }

    @Override
    public boolean isUserDTOEmpty(UserDTO userDTO) {
        return Objects.isNull(userDTO)
                || ObjectUtils.isEmpty(userDTO.getUsername())
                || ObjectUtils.isEmpty(userDTO.getPassword())
                || ObjectUtils.isEmpty(userDTO.getFirstName())
                || ObjectUtils.isEmpty(userDTO.getLastName())
                || ObjectUtils.isEmpty(userDTO.getEmail())
                || userDTO.getGender() == null;
    }
}
