package com.offer.oj.service.impl;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.ForgetPasswordDTO;
import com.offer.oj.domain.dto.LoginDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.service.CacheService;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import com.offer.oj.util.LoginCacheUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
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
    private CacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, VerificationDTO> verificationDTOCache;

    private Cache<String, UserDTO> userDTOCache;

    @Value("${server.ip}")
    private String ip;

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
                message = "Verify email!";
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
        return result;
    }

    @Override
    public Result registerVerifyEmail(VerificationDTO verification) {
        Result result = new Result();
        String message = "";
        if (Objects.isNull(verification) || ObjectUtils.isEmpty(verification.getUsername()) || ObjectUtils.isEmpty(verification.getCode()) || ObjectUtils.isEmpty(verification.getType())) {
            message = "Missing verification code information.";
            log.error(message);
        } else if (verification.getType().equals(EmailTypeEnum.REGISTER.getValue())) {
            log.info("Start verifying email.");
            userDTOCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
            UserDTO userDTO = userDTOCache.get(verification.getUsername());
            if (Objects.isNull(userDTO)) {
                message = "Registration information does not exist or has expired!";
                log.error(message);
                result.setSimpleResult(false, message);
                return result;
            }
            verificationDTOCache = cacheManager.getCache(CacheEnum.REGISTER_CACHE.getValue());
            if (!Objects.isNull(verificationDTOCache.get(userDTO.getUsername())) && !Objects.isNull(verificationDTOCache.get(userDTO.getUsername()).getCode()) && verificationDTOCache.get(userDTO.getUsername()).getCode().equals(verification.getCode())) {
                message = "Email verification successful!";
                log.info(message);
                result = register(userDTO);
                verificationDTOCache.remove(userDTO.getUsername());
            } else {
                message = "Verification code error or expired, email verification failed!";
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
            message = "User registration successful.";
            log.info(message + "{}", userDTO.getUsername());
            userDTOCache.remove(userDTO.getUsername());
        } catch (Exception e) {
            message = "User registration failed.";
            log.error(message + "{}", userDTO.getUsername(), e);
            throw new RuntimeException("User table insertion exception.");
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
                || ObjectUtils.isEmpty(userDTO.getGender());
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
        } else if (!Encryption.checkPassword(loginDTO.getPassword(), userDTO.getPassword())) {
            return new Result(false, "Incorrect Username or Password!");
        }

        // Correct info & Login
        else {
            // SSO
            Integer userId=userMapper.selectIdByUsername(loginDTO.getUsername());                     // Get UserId
            String token= UUID.randomUUID().toString();                  // Get Token
            cacheService.getCache(CacheEnum.LOGIN_CACHE.getValue()).put(token, userId);// Save Token
//            loginCache.put(token, userId);
            Cookie cookie=new Cookie("TOKEN",token);                     // Set Cookie
            cookie.setDomain(ip);
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
        Result result;
        if(null!=cookie){
            Integer userId=LoginCacheUtil.loginUser.get(cookie.getValue());
            OjUser user=ojUserMapper.selectByPrimaryKey(userId);
            result=new Result();
            result.setSuccess(true);
            result.setCode(0);
            result.setMessage("Get User Info Successfully!");
            result.setData(user.getUsername());
        }
        else{
            result=new Result(false, "Cannot Get User Info!");
        }
        return result;
    }

    @Override
    public Result forgetPassword(ForgetPasswordDTO forgetPasswordDTO) {
        Result result=new Result();
        String message = "";
        UserDTO user = userMapper.selectByUsername(forgetPasswordDTO.getUsername());
        String email = user.getEmail();
        if (Objects.isNull(forgetPasswordDTO.getUsername()) || Objects.isNull(forgetPasswordDTO.getEmail())) {
            message = "Incomplete Information!";
            log.error(message);
            result.setSimpleResult(false, message);
        }
        else if (Objects.isNull(user)) {
            message = "Incorrect Username!";
            log.error(message);
            result.setSimpleResult(false, message);
        } else if (!email.equals(forgetPasswordDTO.getEmail())) {
            message = "Incorrect Username or Email!";
            log.error(message);
            result.setSimpleResult(false, message);
        }
        else{
            //发送邮件
            message = "send email successfully!";
            log.info(message);
            result.setSimpleResult(true, message);
        }
        return result;
    }

    @Override
    public String verifyRole(Integer id, String role) {
        if (id == null){
            log.info("The user has wrong qualification!");
            return null;
        }
        UserDTO userDTO = userMapper.selectById(id);
        if (userDTO == null){
            log.info("No such user! userid: {}", id);
            return null;
        }
        if (userDTO.getRole().equals(role)){
            return userDTO.getUsername();
        }else{
            return null;
        }
    }
}
