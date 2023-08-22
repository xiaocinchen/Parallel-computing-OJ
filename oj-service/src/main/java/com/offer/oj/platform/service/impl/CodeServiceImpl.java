package com.offer.oj.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.CacheException;
import com.offer.oj.domain.Result;
import com.offer.oj.platform.MQ.sender.CodeMQSender;
import com.offer.oj.dao.CodeMapper;
import com.offer.oj.domain.dto.CodeStageDTO;
import com.offer.oj.domain.dto.SubmitCodeDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.domain.enums.CodeStatusEnum;
import com.offer.oj.domain.enums.SeparatorEnum;
import com.offer.oj.domain.query.CodeInnerQuery;
import com.offer.oj.domain.query.CodeResultListQuery;
import com.offer.oj.domain.dto.CodeSimpleResultDTO;
import com.offer.oj.common.service.CacheService;
import com.offer.oj.platform.service.CodeService;
import com.offer.oj.common.util.LockUtil;
import com.offer.oj.common.util.ThreadPoolUtil;
import com.offer.oj.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CodeMQSender codeMQSender;

    @Autowired
    private CacheService cacheService;

    private static final String CODE_SUBMIT_LOCK = "CODE_SUBMIT_LOCK";

    @Override
    public Result submitCode(SubmitCodeDTO submitCodeDTO) {
        String md5 = DigestUtils.md5DigestAsHex(JSON.toJSONString(submitCodeDTO).getBytes());
        String lockKey = CODE_SUBMIT_LOCK + md5;
        if (LockUtil.isLocked(lockKey,5L)){
            log.warn("Please do not resubmit.");
            return new Result(false, "Please do not resubmit.", -2);
        }
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
        codeInnerQuery.setQuestionId(submitCodeDTO.getQuestionId());
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
    public Result submitStageCode(CodeStageDTO codeStageDTO) {
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
    public Result<CodeStageDTO> getStageCode(CodeStageDTO codeStageDTO) {
        Result<CodeStageDTO> result = new Result<>();
        try {
            codeStageDTO = (CodeStageDTO) cacheService.getCache(CacheEnum.STAGE_CODE_CACHE.getValue()).get(codeStageDTO.getAuthorId() + SeparatorEnum.UNDERLINE.getSeparator() + codeStageDTO.getType().getValue());
        } catch (Exception e) {
            throw new CacheException(e);
        }
        result.setData(codeStageDTO);
        result.setCode(0);
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result<List<CodeSimpleResultDTO>> getCodeResult(CodeResultListQuery codeResultListQuery) {
        Result<List<CodeSimpleResultDTO>> result = new Result<>();
        try {
            List<CodeSimpleResultDTO> codeSimpleResultDTOS = codeMapper.queryCodeResultListByAuthorId(codeResultListQuery);
            AtomicReference<Integer> i = new AtomicReference<>(0);
            codeSimpleResultDTOS.forEach(codeSimpleResultDTO-> codeSimpleResultDTO.setId(i.getAndSet(i.get() + 1)));
            result.setData(codeSimpleResultDTOS);
            result.setSimpleResult(true,0);
        } catch (Exception e) {
            throw new RuntimeException("Query Code Simple Result" + codeResultListQuery, e);
        }
        return result;
    }


}
