package com.offer.oj.service.impl;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.*;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.EncryptionUtil;
import com.offer.oj.util.KaptchaUtil;
import com.offer.oj.util.ThreadPoolUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.UUID;

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
    private CacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, VerificationDTO> verificationDTOCache;

    private Cache<String, UserDTO> userDTOCache;

    @Value("${server.ip}")
    private String ip;

    @Override
    public Result<String> registerSendEmail(UserDTO userDTO, HttpServletResponse response) {
        Result<String> result = new Result<>();
        String message = "";
        if (!GenderEnum.getGenderSet().contains(userDTO.getGender())) {
            message = "Unknown gender! " + userDTO.getGender();
            result.setSimpleResult(false, message, -1);
            log.warn(message);
        } else if (userMapper.selectByUsername(userDTO.getUsername()) != null) {
            message = "Username already exists! " + userDTO.getUsername();
            result.setSimpleResult(false, message, -2);
            log.warn(message);
        } else if (userMapper.selectByEmail(userDTO.getEmail()) != null) {
            message = "Email already exists! " + userDTO.getEmail();
            result.setSimpleResult(false, message, -3);
            log.warn(message);
        } else {
            try {
                String tempLicence = UUID.randomUUID().toString();
                cacheService.getCache(CacheEnum.REGISTER_CACHE.getValue()).put(userDTO.getUsername(), tempLicence);
                cacheService.getCache(CacheEnum.USER_CACHE.getValue()).put(userDTO.getUsername(), userDTO);
                message = "Verify email!";
                KaptchaDTO kaptchaDTO = KaptchaUtil.getKaptcha();
                EmailDTO emailDTO = new EmailDTO();
                BeanUtils.copyProperties(userDTO, emailDTO);
                emailDTO.setCode(kaptchaDTO.getCode());
                emailDTO.setSubject("This is a registration verification email!");
                emailDTO.setType(EmailTypeEnum.REGISTER);
                ThreadPoolUtil.sendMQThreadPool.execute(() -> emailService.sendVerifyEmail(emailDTO));
                cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).put(userDTO.getUsername() + SeparatorEnum.UNDERLINE.getSeparator() + EmailTypeEnum.REGISTER.getValue(), kaptchaDTO.getCode());
                Cookie cookie = new Cookie(CookieEnum.TEMP_LICENCE.getValue(), tempLicence);                     // Set Cookie
                cookie.setDomain(ip);
                cookie.setPath("/");
                response.addCookie(cookie);
                log.info(message);
                result.setSimpleResult(true, message, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public Result resendVerifyEmail(String username, String tempLicence) {
        Result result = new Result();
        String licence = (String) cacheService.getCache(CacheEnum.REGISTER_CACHE.getValue()).get(username);
        if (ObjectUtils.isEmpty(licence)) {
            result.setSimpleResult(false, "User info has expired.", -1);
        } else if (!licence.equals(tempLicence)) {
            result.setSimpleResult(false, "Temp licence error.", -2);
        } else {
            EmailDTO emailDTO = new EmailDTO();
            BeanUtils.copyProperties(cacheService.getCache(CacheEnum.USER_CACHE.getValue()).get(username), emailDTO);
            KaptchaDTO kaptcha = KaptchaUtil.getKaptcha();
            emailDTO.setCode(kaptcha.getCode());
            emailDTO.setType(EmailTypeEnum.REGISTER);
            ThreadPoolUtil.sendMQThreadPool.execute(()->emailService.sendVerifyEmail(emailDTO));
            cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).put(username + SeparatorEnum.UNDERLINE.getSeparator() + EmailTypeEnum.REGISTER.getValue(), kaptcha.getCode());
            result.setSimpleResult(true, "Resend OK.", 0);
        }
        return result;
    }

    @Override
    public Result registerVerifyEmail(VerificationDTO verification) {
        Result result = new Result();
        String message = "";
        if (verification.getType().equals(EmailTypeEnum.REGISTER.getValue())) {
            log.info("Start verifying email.");
            UserDTO userDTO = (UserDTO) cacheService.getCache(CacheEnum.USER_CACHE.getValue()).get(verification.getUsername());
            String code = (String) cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).get(verification.getVerificationKey());
            if (ObjectUtils.isEmpty(userDTO) || ObjectUtils.isEmpty(code)) {
                message = "Verification code has expired.";
                log.error(message);
                result.setSimpleResult(false, message, -1);
                return result;
            } else if (!code.equals(verification.getCode())) {
                message = "Verification code error.";
                result.setSimpleResult(false, message, -2);
                log.error(message + "{}", verificationDTOCache.get(userDTO.getUsername()));
            } else {
                result = register(userDTO);
                cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).remove(verification.getVerificationKey());
                cacheService.getCache(CacheEnum.REGISTER_CACHE.getValue()).remove(userDTO.getUsername());
            }
        } else {
            result.setSimpleResult(false, "Verify type error.", -3);
        }
        return result;
    }


    @Override
    public Result login(LoginDTO loginDTO, HttpServletResponse response) {

        // Incomplete input
        if (null == loginDTO.getUsername() || null == loginDTO.getPassword()) {
            return new Result(false, "Incomplete Login Information!");
        }

        // Incorrect info
        UserDTO userDTO = userMapper.selectByUsername(loginDTO.getUsername());
        if (null == userDTO) {
            return new Result(false, "Incorrect Username or Password!");
        } else if (!EncryptionUtil.checkPassword(loginDTO.getPassword(), userDTO.getPassword())) {
            return new Result(false, "Incorrect Username or Password!");
        }

        // Correct info & Login
        else {
            // SSO
            Integer userId = userMapper.selectIdByUsername(loginDTO.getUsername());                     // Get UserId
            String token = UUID.randomUUID().toString();                  // Get Token
            cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).put(token, userId);// Save Token
            Cookie cookie = new Cookie(CookieEnum.TOKEN.getValue(), token);                     // Set Cookie
            cookie.setDomain(ip);
            cookie.setPath("/");
            response.addCookie(cookie);
            // Return result
            Result result = new Result();
            result.setSimpleResult(true, "Login successfully!", 0);
            return result;
        }
    }

    @Override
    public boolean isLogin(Cookie cookie) {
        return null != cookie && cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(cookie.getValue()) != null;
    }

    @Override
    public Result logout(Cookie cookie) {
        if (null != cookie) {
            cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).remove(cookie.getValue());
        }
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(0);
        result.setMessage("Logout Successfully!");
        return result;
    }


    @Override
    public Result userInfo(Cookie cookie) {
        Result result;
        if (null != cookie) {
            Integer userId = (Integer) cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).get(cookie.getValue());
            OjUser user = ojUserMapper.selectByPrimaryKey(userId);
            result = new Result();
            result.setSuccess(true);
            result.setCode(0);
            result.setMessage("Get User Info Successfully!");
            result.setData(user.getUsername());
        } else {
            result = new Result(false, "Cannot Get User Info!");
        }
        return result;
    }

    @Override
    public Result forgetPassword(ForgetPasswordDTO forgetPasswordDTO) {
        Result result = new Result();
        String message = "";
        UserDTO user;
        if (Objects.isNull(user = userMapper.selectByUsername(forgetPasswordDTO.getUsername()))) {
            message = "Incorrect Username!";
            log.warn(message);
            result.setSimpleResult(false, message, -1);
        } else {
            String email = user.getEmail();
            if (!email.equals(forgetPasswordDTO.getEmail())) {
                message = "Incorrect Username or Email!";
                log.warn(message);
                result.setSimpleResult(false, message, -2);
            } else {
                //发送邮件
                EmailDTO emailDTO = new EmailDTO();
                BeanUtils.copyProperties(user, emailDTO);
                KaptchaDTO kaptcha = KaptchaUtil.getKaptcha();
                emailDTO.setCode(kaptcha.getCode());
                emailService.sendVerifyEmail(emailDTO);
                message = "send email successfully!" + user.getEmail();
                log.info(message);
                result.setSimpleResult(true, 0);
            }
        }
        return result;
    }

    @Override
    public Boolean verifyRole(UserIdentityDTO userIdentityDTO, String role) {
        return userIdentityDTO != null && role.equals(userIdentityDTO.getRole());
    }

    @Override
    public UserIdentityDTO getUserIdentity(Integer userId) {
        if (userId == null) {
            log.info("The user has wrong qualification!");
            return null;
        }
        UserDTO userDTO = userMapper.selectById(userId);
        if (userDTO == null) {
            log.info("No such user! userid: {}", userId);
            return null;
        }
        return new UserIdentityDTO(userId, userDTO.getUsername(), userDTO.getRole());
    }


    /***** private method *****/
    private Result register(UserDTO userDTO) {
        Result result = new Result();
        String message = "";
        userDTO.setPassword(EncryptionUtil.hashPassword(userDTO.getPassword()));
        OjUser ojUser = new OjUser();
        BeanUtils.copyProperties(userDTO, ojUser);
        try {
            ojUserMapper.insertSelective(ojUser);
            message = "User registration successful.";
            log.info(message + "{}", userDTO.getUsername());
            cacheService.getCache(CacheEnum.USER_CACHE.getValue()).remove(userDTO.getUsername());
        } catch (Exception e) {
            message = "User registration failed.";
            log.error(message + "{}", userDTO.getUsername(), e);
            throw new RuntimeException("User table insertion exception.");
        }
        result.setSimpleResult(true, message, 0);
        return result;
    }
}
