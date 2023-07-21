package com.offer.oj.MQ.sender;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.config.RabbitMQConfig;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.MQExchangeEnum;
import com.offer.oj.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheService cacheService;

    public void sendRegisterVerifyEmailMQ(EmailDTO emailDTO) {
        Cache<String, UserDTO> usernameCache = cacheManager.getCache(CacheEnum.USER_CACHE.getValue());
        if (usernameCache.get(emailDTO.getUsername()) != null) {
            rabbitTemplate.convertAndSend(MQExchangeEnum.EMAIL_EXCHANGE.getValue(), "email.verify", emailDTO);
        } else {
            log.error("注册信息不存在或已过期!");
        }
    }

    public void sendForgetVerifyEmailMQ(EmailDTO emailDTO) {
        rabbitTemplate.convertAndSend(MQExchangeEnum.EMAIL_EXCHANGE.getValue(), "email.verify", emailDTO);
    }

}
