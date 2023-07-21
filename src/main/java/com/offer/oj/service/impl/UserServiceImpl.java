package com.offer.oj.service.impl;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.domain.enums.GenderEnum;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.KaptchaService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.EncryptionUtil;
import com.offer.oj.util.ThreadPoolUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
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

    @Autowired
    private KaptchaService kaptchaService;

    @Override
    public Result<String> registerSendEmail(UserDTO userDTO) {
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
                Cache<String, UserDTO> usernameCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
                usernameCache.put(userDTO.getUsername(), userDTO);
                message = "Verify email!";
//                ThreadPoolUtil.sendMQThreadPool.execute(() -> emailService.sendRegisterVerifyEmail(userDTO));
                emailService.sendRegisterVerifyEmail(userDTO);
                log.info(message);
                result.setSimpleResult(true, message, 0);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                result.setSimpleResult(false, "Unknown Exception", -4);
            }
        }
        return result;
    }

    @Override
    public Result registerVerifyEmail(VerificationDTO verification) {
        Result result = new Result();
        String message = "";
        if (verification.getType().equals(EmailTypeEnum.REGISTER.getValue())) {
            log.info("Start verifying email.");
            userDTOCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
            UserDTO userDTO = userDTOCache.get(verification.getUsername());
            if (Objects.isNull(userDTO)) {
                message = "Registration information does not exist or has expired!";
                log.error(message);
                result.setSimpleResult(false, message, -1);
                return result;
            }
            verificationDTOCache = cacheManager.getCache(CacheEnum.REGISTER_CACHE.getValue());
            if (!Objects.isNull(verificationDTOCache.get(userDTO.getUsername())) && verificationDTOCache.get(userDTO.getUsername()).getCode().equals(verification.getCode())) {
                message = "Email verification successful!";
                log.info(message);
                result = register(userDTO);
                verificationDTOCache.remove(userDTO.getUsername());
            } else {
                message = "Verification code error or expired, email verification failed!";
                result.setSimpleResult(false, message, -2);
                log.error(message + "{}", verificationDTOCache.get(userDTO.getUsername()));
            }
        }
        return result;
    }

    public Result register(UserDTO userDTO) {
        Result result = new Result();
        String message = "";
        userDTO.setPassword(EncryptionUtil.hashPassword(userDTO.getPassword()));
        OjUser ojUser = new OjUser();
        BeanUtils.copyProperties(userDTO, ojUser);
        try {
            ojUserMapper.insertSelective(ojUser);
            message = "User registration successful.";
            log.info(message + "{}", userDTO.getUsername());
            userDTOCache.remove(userDTO.getUsername());
        } catch (Exception e) {
            message = "User registration failed.";
            log.error(message + "{}", userDTO.getUsername(), e);
            throw new RuntimeException("User table insertion exception.");
        }
        result.setSimpleResult(true, message, 0);
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
            Cookie cookie = new Cookie("TOKEN", token);                     // Set Cookie
            cookie.setDomain(ip);
            cookie.setPath("/");
            response.addCookie(cookie);
            System.out.println(cookie.getValue());
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
                emailService.sendModifyPasswordEmail(user);
                message = "send email successfully!" + user.getEmail();
                log.info(message);
                result.setSimpleResult(true, message, 0);
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

    @Override
    public Result modifyPassword(ModifyPasswordDTO modifyPasswordDTO) {
        Result result = new Result();
        if(kaptchaService.checkKaptcha(modifyPasswordDTO.getKaptchaCode()).getCode().equals(0)){
            UserDTO userDTO = userMapper.selectByUsername(modifyPasswordDTO.getUsername());
            if (ObjectUtils.isEmpty(userDTO)){
                result.setMessage("Incorrect Username!");
                result.setSimpleResult(false,-1);
            } else if (modifyPasswordDTO.getPassword().equals(userDTO.getPassword())){
                result.setMessage("Make sure the password is different from the original one!");
                result.setSimpleResult(false,-1);
            }
            else {
                userDTO.setPassword(EncryptionUtil.hashPassword(modifyPasswordDTO.getPassword()));
                userMapper.updateUserInfo(userDTO);
                result.setSimpleResult(true, 0);
                result.setMessage("Modify password successfully!");
            }
        }
        else {
            log.error("Please enter the correct verification code!");
            result.setSimpleResult(false,-2);
        }
        return result;
    }
}
