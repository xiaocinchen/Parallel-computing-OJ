package com.offer.oj.MQ.listener;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
@Slf4j
@Service
public class EmailMQListener {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, VerificationDTO> verificationDTOCache;

    private Cache<String, UserDTO> userDTOCache;

    private static final String SENDER = "707103676@qq.com";


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "email_queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "email_exchange", type = ExchangeTypes.TOPIC),
            key = "email.#"), ackMode = "MANUAL")
    @RabbitHandler
    public void listenRegisterVerifyEmail(@Payload EmailDTO emailDTO, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
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
            verificationDTO.setType(EmailTypeEnum.REGISTER.getValue());
            verificationDTOCache= cacheManager.getCache(CacheEnum.REGISTER_CACHE.getValue());
            verificationDTOCache.put(verificationDTO.getUsername(), verificationDTO); // to be changed;
            log.info("邮件已发送 {}",emailDTO.getUsername());
        } catch (Exception e) {
            log.error("邮件发送失败{}: ", String.valueOf(e));
            userDTOCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
            userDTOCache.remove(emailDTO.getUsername());
//            channel.basicNack(deliveryTag, false, true);
//            throw new RuntimeException("邮件发送失败");
        }
        channel.basicAck(deliveryTag, false);
    }
}
