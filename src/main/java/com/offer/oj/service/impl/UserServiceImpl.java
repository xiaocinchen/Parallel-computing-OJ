package com.offer.oj.service.impl;


import com.offer.oj.dao.Result;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    @Autowired
    private OjUserMapper ojUserMapper;

    @Override
    public Result registerSendEmail(UserDTO userDTO, boolean isStudent) {
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
    public Result registerVerifyEmail(VerificationDTO verification) {
        String message = "";
        if (Objects.isNull(verification) || StringUtils.isEmpty(verification.getUsername()) || StringUtils.isEmpty(verification.getCode()) || StringUtils.isEmpty(verification.getType())) {
            message = "验证码信息缺失";
            log.error(message);
        } else if (verification.getType().equals(EmailTypeEnum.REGISTER.getValue())) {
            log.info("开始验证邮件");

        }
        return null;
    }

    @Override
    public boolean isUserDTOEmpty(UserDTO userDTO) {
        return Objects.isNull(userDTO)
                || StringUtils.isEmpty(userDTO.getUsername())
                || StringUtils.isEmpty(userDTO.getPassword())
                || StringUtils.isEmpty(userDTO.getFirstName())
                || StringUtils.isEmpty(userDTO.getLastName())
                || StringUtils.isEmpty(userDTO.getEmail())
                || userDTO.getGender() == null;
    }
}
