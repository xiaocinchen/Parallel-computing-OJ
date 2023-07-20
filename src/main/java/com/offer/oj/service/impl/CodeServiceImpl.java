package com.offer.oj.service.impl;

import com.alicp.jetcache.CacheException;
import com.offer.oj.MQ.sender.CodeMQSender;
import com.offer.oj.dao.CodeMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.CodeStageDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.service.CacheService;
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

    @Autowired
    private CacheService cacheService;

    @Override
    public Result submitCode(SubmitCodeDTO submitCodeDTO) {
        Result result = new Result<>();
        String message = "";
        CodeInnerQuery codeInnerQuery = new CodeInnerQuery();
        if (submitCodeDTO.getIsResult()) {
            submitCodeDTO.setFileName(
                    submitCodeDTO.getQuestionId()
                            + SeparatorEnum.UNDERLINE.getSeparator()
                            + submitCodeDTO.getType().getValue());
            codeMapper.selectAndDeleteCodeByName(submitCodeDTO.getFileName());
        } else {
            submitCodeDTO.setFileName(
                    submitCodeDTO.getQuestionId()
                            + SeparatorEnum.UNDERLINE.getSeparator()
                            + submitCodeDTO.getAuthorId()
                            + SeparatorEnum.UNDERLINE.getSeparator()
                            + TimeUtil.getUniqueSequence());
        }
        BeanUtils.copyProperties(submitCodeDTO, codeInnerQuery);
        codeInnerQuery.setType(submitCodeDTO.getType().getValue());
        codeInnerQuery.setStatus(CodeStatusEnum.PENDING.getStatus());
        if (codeMapper.submitCode(codeInnerQuery)) {
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

    @Override
    public Result stageCodeSubmit(CodeStageDTO codeStageDTO) {
        try {
            cacheService.getCache(CacheEnum.STAGE_CODE_CACHE.getValue()).put(
                    codeStageDTO.getAuthorId() + SeparatorEnum.UNDERLINE.getSeparator() + codeStageDTO.getType().getValue(),
                    codeStageDTO.getContent());
        } catch (Exception e) {
            throw new CacheException(e);
        }

        return new Result<>(true, "code stage ok", 0);
    }

    @Override
    public Result<CodeStageDTO> stageCodeGet(CodeStageDTO codeStageDTO){
        Result<CodeStageDTO> result = new Result<>();
        try{
            codeStageDTO = (CodeStageDTO) cacheService.getCache(CacheEnum.STAGE_CODE_CACHE.getValue()).get(codeStageDTO.getAuthorId() + SeparatorEnum.UNDERLINE.getSeparator() + codeStageDTO.getType().getValue());
        } catch (Exception e){
            throw new CacheException(e);
        }
        result.setData(codeStageDTO);
        result.setCode(0);
        result.setSuccess(true);
        return result;
    }


}
