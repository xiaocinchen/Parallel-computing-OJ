package com.offer.oj.MQ.sender;

import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.MQExchangeEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;

@Slf4j
@Component
public class CodeMQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${code.path}")
    private String BASIC_PATH;

    public void sendCodeForJudgeMQ(SubmitCodeDTO submitCodeDTO) {
        try {
            FileWriter writer = new FileWriter(BASIC_PATH + submitCodeDTO.getFileName() + SeparatorEnum.DOT.getSeparator() + submitCodeDTO.getType().getValue());
            writer.write("");
            writer.write(submitCodeDTO.getContent());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error("Write code into file Exception.");
            e.printStackTrace();
        }
        submitCodeDTO.clearContent();
        rabbitTemplate.convertAndSend(MQExchangeEnum.CODE_EXCHANGE.getValue(), "code.write", submitCodeDTO);
    }
}
