package com.offer.oj.platform.MQ.sender;

import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CodeTypeEnum;
import com.offer.oj.domain.enums.MQExchangeEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class CodeMQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${code.source.path}")
    private String BASIC_PATH;

    @Value("${code.result.path}")
    private String BASIC_RESULT_PATH;

    public void sendCodeForJudgeMQ(SubmitCodeDTO submitCodeDTO) {
        String codeFilePath;
        if (submitCodeDTO.getIsResult()) {
            codeFilePath = BASIC_RESULT_PATH;
        } else {
            codeFilePath = BASIC_PATH + submitCodeDTO.getAuthorId() + SeparatorEnum.SLASH.getSeparator();
        }
        codeFilePath += "question" + submitCodeDTO.getQuestionId() + SeparatorEnum.SLASH.getSeparator() + "code/";
        FileUtil.getDir(codeFilePath);
        String codeFileName;
        if (submitCodeDTO.getType().equals(CodeTypeEnum.JAVA)) {
            codeFilePath = codeFilePath + submitCodeDTO.getFileName() + SeparatorEnum.SLASH.getSeparator();
            FileUtil.getDir(codeFilePath);
            codeFileName = codeFilePath + "Main.java";
        } else {
            codeFileName = codeFilePath + submitCodeDTO.getFileName() + SeparatorEnum.DOT.getSeparator() + submitCodeDTO.getType().getValue();
        }
        String codeContent = submitCodeDTO.getContent();
        try {
            writeFile(codeFileName, codeContent);
        } catch (Exception e) {
            log.error("Write code into file Exception. {}", submitCodeDTO.getFileName());
            throw new RuntimeException(e.getMessage());
        }
        submitCodeDTO.clearContent();
        rabbitTemplate.convertAndSend(MQExchangeEnum.CODE_EXCHANGE.getValue(), "code.judge", submitCodeDTO);
    }


    /***** private method *****/
    private void writeFile(String codeFileName, String codeContent) {
        FileWriter writer;
        try {
            writer = new FileWriter(codeFileName);
            writer.write("");
            writer.write(codeContent);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
