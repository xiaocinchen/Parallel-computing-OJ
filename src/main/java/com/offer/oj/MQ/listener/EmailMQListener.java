package com.offer.oj.MQ.listener;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.domain.enums.KaptchaEnum;
import com.offer.oj.service.CacheService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class EmailMQListener {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CacheService cacheService;

    private Cache<String, UserDTO> userDTOCache;

    @Value("${spring.mail.username}")
    private String address;

//    private final String SENDER = "OJ<"+address+">";


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email_queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "email_exchange", type = ExchangeTypes.TOPIC),
            key = "email.#"), ackMode = "MANUAL")
    @RabbitHandler
    public void listenRegisterVerifyEmail(@Payload EmailDTO emailDTO, @Headers Map<String, Object> headers, Channel channel) {
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        String SENDER = "OJ<" + address + ">";
        SimpleMailMessage message = new SimpleMailMessage();
        VerificationDTO verificationDTO = new VerificationDTO();
        message.setFrom(SENDER);
        message.setTo(emailDTO.getEmail());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        try {
            mailSender.send(message);
            verificationDTO.setUsername(emailDTO.getUsername());
            verificationDTO.setCode(emailDTO.getCode());
            if (emailDTO.getType().equals(EmailTypeEnum.REGISTER)) {
                verificationDTO.setType(KaptchaEnum.REGISTER);
            } else if (emailDTO.getType().equals(EmailTypeEnum.CHANGE_PASSWORD)){
                verificationDTO.setType(KaptchaEnum.FORGET_PASSWORD);
            }
            cacheService.getCache(CacheEnum.KAPTCHA_CACHE.getValue()).put(verificationDTO.getVerificationKey(), verificationDTO.getCode());
            log.info("邮件已发送 {}", emailDTO.getUsername());
        } catch (Exception e) {
            log.error("邮件发送失败{}: ", String.valueOf(e));
//            cacheService.getCache(CacheEnum.USER_CACHE.getValue()).remove(emailDTO.getUsername());
        }
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
