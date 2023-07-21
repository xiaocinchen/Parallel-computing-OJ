package com.offer.oj.service.impl;

import com.offer.oj.MQ.sender.EmailMQSender;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.KaptchaService;
import com.offer.oj.util.KaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailMQSender emailMQSender;

    @Override
    public void sendVerifyEmail(EmailDTO emailDTO) {
        Optional.ofNullable(emailDTO).orElseThrow(()->new RuntimeException("EmailDto is null"));
        String content = "Your code is " + emailDTO.getCode();
        emailDTO.setHtml(false);
        emailDTO.setContent(content);
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
