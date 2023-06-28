package com.offer.oj.MQ.sender;

import com.offer.oj.config.RabbitMQConfig;
import com.offer.oj.domain.dto.EmailDTO;
import com.offer.oj.domain.dto.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendRegisterVerifyEmailMQ(EmailDTO emailDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.ITEM_TOPIC_EXCHANGE, "email.verify", emailDTO);
    }
}
