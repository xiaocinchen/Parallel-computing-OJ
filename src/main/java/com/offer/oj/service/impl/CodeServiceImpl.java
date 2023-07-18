package com.offer.oj.service.impl;

import com.offer.oj.MQ.sender.CodeMQSender;
import com.offer.oj.dao.CodeMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.service.CodeService;
import com.offer.oj.util.ThreadPoolUtil;
import com.offer.oj.util.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CodeMQSender codeMQSender;

    @Override
    public Result submitCode(SubmitCodeDTO submitCodeDTO){
        Result result = new Result<>();
        String message = "";
        CodeInnerQuery codeInnerQuery = new CodeInnerQuery();
        submitCodeDTO.setFileName(
                submitCodeDTO.getQuestionId()
                        +SeparatorEnum.UNDERLINE.getSeparator()
                        +submitCodeDTO.getAuthorId()
                        +SeparatorEnum.UNDERLINE.getSeparator()
                        +TimeUtil.getUniqueSequence());
        BeanUtils.copyProperties(submitCodeDTO, codeInnerQuery);
        codeInnerQuery.setType(submitCodeDTO.getType().getValue());

        if (codeMapper.submitCode(codeInnerQuery)){
            ThreadPoolUtil.sendMQThreadPool.execute(() -> codeMQSender.sendCodeForJudgeMQ(submitCodeDTO));
            message = "Submit success.";
            result.setSuccess(true);
        } else {
            message = "Submit fail.";
            result.setSuccess(false);
        }
        result.setMessage(message);
        return result;
    }
}
