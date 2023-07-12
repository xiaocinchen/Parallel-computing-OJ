package com.offer.oj.service.impl;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private OjUserMapper ojUserMapper;

    @Override
    public Result<String> addQuestion(Integer userId, QuestionDTO questionDTO){
        Result<String> result = new Result<>();
        String message = "";
        OjUser user=ojUserMapper.selectByPrimaryKey(userId);
        if(user.getRole().equals("teacher")) {
            if (isQuestionDTOEmpty(questionDTO)) {
                message = "Incomplete question information";
                log.error(message);
            } else if (questionDTO.getTitle().length() < 2 || questionDTO.getTitle().length() > 25) {
                message = "The title should be between 2 and 25 characters! " + questionDTO.getTitle();
                log.error(message);
            } else if (questionDTO.getDescription().length() < 20 || questionDTO.getDescription().length() > 1000) {
                message = "description should be between 20 and 100 characters!";
                log.error(message);
            } else if (!isValidUrl(questionDTO.getPictureUrl())) {
                message = "picture_url is invalid !" + questionDTO.getPictureUrl();
                log.error(message);
            }
            if (message.isEmpty()) {
                OjQuestion ojQuestion = new OjQuestion();
                try {
                    questionDTO.setModifier(user.getUsername());
                    BeanUtils.copyProperties(questionDTO, ojQuestion);
                    questionMapper.insertSelective(ojQuestion);
                    result.setSuccess(true);
                    message = "Submit successfully!";
                    log.info(message);
                } catch (Exception e) {
                    log.error(String.valueOf(e));
                    result.setSuccess(false);
                }
            } else {
                result.setSuccess(false);
            }
            result.setMessage(message);
        }
        else{
            message = "Permission denied!";
            result.setSuccess(false);
            result.setMessage(message);
        }
        return result;
    }


    @Override
    public List<QuestionDTO> selectQuestion(String title) {
        //精准查找
        List<QuestionDTO> questionDTO = questionMapper.selectByTitle(title);
        if (! ObjectUtils.isEmpty(questionDTO)){
            return questionDTO;
        }
        //模糊查找
        else {
            List<QuestionDTO> questionDTOList = questionMapper.fuzzySelectByTitle(title);
            return questionDTOList;
        }
    }

    @Override
    public boolean isQuestionDTOEmpty(QuestionDTO questionDTO) {
        return Objects.isNull(questionDTO)
                || ObjectUtils.isEmpty(questionDTO.getTitle())
                || ObjectUtils.isEmpty(questionDTO.getDescription())
                || ObjectUtils.isEmpty(questionDTO.getPictureUrl());
    }

    @Override
    public boolean isValidUrl(String url){
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        if (uri.getHost() == null) {
            return false;
        }
        if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
            return true;
        }
        return false;
    }
}
