package com.offer.oj.service.impl;

import com.offer.oj.MQ.sender.EmailMQSender;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailMQSender emailMQSender;

    @Override
    public void sendRegisterVerifyEmail(UserDTO userDTO) {
        EmailDTO emailDTO = new EmailDTO();
        BeanUtils.copyProperties(userDTO, emailDTO);
        String content = "Your code is 2017.";
        emailDTO.setCode("2017");
        emailDTO.setHtml(false);
        emailDTO.setContent(content);
        emailDTO.setSubject("This is a registration verification email!");
        emailMQSender.sendRegisterVerifyEmailMQ(emailDTO);
    }
}
