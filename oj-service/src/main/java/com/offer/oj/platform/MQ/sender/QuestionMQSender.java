package com.offer.oj.platform.MQ.sender;

import com.offer.oj.domain.dto.QuestionSynDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.MQExchangeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QuestionMQSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendQuestionFuzzySearchMQ(Integer id, String key) {
        rabbitTemplate.convertAndSend(MQExchangeEnum.QUESTION_EXCHANGE.getValue(), "question.fuzzy.id", new QuestionSynDTO(id, key));
    }

    public void sendQuestionModifyMQ(Integer id) {
        rabbitTemplate.convertAndSend(MQExchangeEnum.QUESTION_EXCHANGE.getValue(), "question.fuzzy.cache", id);
    }
}
