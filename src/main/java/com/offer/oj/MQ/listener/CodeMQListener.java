package com.offer.oj.MQ.listener;

import com.offer.oj.dao.CodeMapper;
import com.offer.oj.domain.dto.CodeResultDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.util.pack.DockerUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CodeMQListener {

    @Autowired
    private DockerUtil dockerUtil;

    @Autowired
    private CodeMapper codeMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "code_judge_queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "code_exchange", type = ExchangeTypes.TOPIC),
            key = "code.judge"), ackMode = "MANUAL")
    @RabbitHandler
    public void listenJudgeCode(@Payload SubmitCodeDTO submitCodeDTO, @Headers Map<String, Object> headers, Channel channel) {
        long deliveryTag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            CodeResultDTO codeResultDTO = dockerUtil.executeCodeAndGetResult(submitCodeDTO);
            CodeInnerQuery query = new CodeInnerQuery();
            query.setFileName(codeResultDTO.getFileName());
            query.setResult(codeResultDTO.getResult());
            query.setStatus(codeResultDTO.getStatus());
            query.setExecutionTime(codeResultDTO.getTime());
            codeMapper.updateCodeByFileName(query);
        } catch (Throwable e) {
            throw new ListenerExecutionFailedException("Judge Code Listener Exception.", e);
        }
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
