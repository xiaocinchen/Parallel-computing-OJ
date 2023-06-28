package com.offer.oj.MQ.listener;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.CacheEnum;
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

    private Cache<VerificationDTO, String> verificationDTOCache;

    private static final String SENDER = "707103676@qq.com";

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder(CacheEnum.USER_CACHE.getValue())
                .expire(Duration.ofSeconds(70))
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        verificationDTOCache = cacheManager.getOrCreateCache(qc);
    }

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
            verificationDTOCache.put(verificationDTO, emailDTO.getCode()); // to be changed;
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
