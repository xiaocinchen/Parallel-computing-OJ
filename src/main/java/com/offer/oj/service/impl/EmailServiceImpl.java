package com.offer.oj.service.impl;

import com.offer.oj.MQ.sender.EmailMQSender;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.KaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailMQSender emailMQSender;

    @Autowired
    private KaptchaService kaptchaService;

    @Override
    public void sendRegisterVerifyEmail(UserDTO userDTO) {
        EmailDTO emailDTO = new EmailDTO();
        BeanUtils.copyProperties(userDTO, emailDTO);
        String code = kaptchaService.getKaptcha().getData().getCode();
        String content = "Your code is " + code;
        emailDTO.setCode(code);
        emailDTO.setHtml(false);
        emailDTO.setContent(content);
        emailDTO.setSubject("This is a registration verification email!");
        emailMQSender.sendRegisterVerifyEmailMQ(emailDTO);
    }

    @Override
    public void sendModifyPasswordEmail(UserDTO userDTO) {
        EmailDTO emailDTO = new EmailDTO();
        BeanUtils.copyProperties(userDTO, emailDTO);
        String code = kaptchaService.getKaptcha().getData().getCode();
        String content = "Your code is " + code;
        System.out.println(content);
        emailDTO.setCode(code);
        emailDTO.setHtml(false);
        emailDTO.setContent(content);
        emailDTO.setSubject("This is a modify password email!");
        emailMQSender.sendRegisterVerifyEmailMQ(emailDTO);
    }
}
