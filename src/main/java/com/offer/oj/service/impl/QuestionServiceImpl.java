package com.offer.oj.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.dao.mapper.OjUserMapper;
import com.offer.oj.domain.OjQuestion;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.enums.CacheEnum;
import com.offer.oj.service.QuestionService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.offer.oj.util.LoginCacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OjUserMapper ojUserMapper;

    @Autowired
    private OjQuestionMapper ojQuestionMapper;

    private Cache<String, UserDTO> userDTOCache;

    @Override
    public Result<String> add_question(Cookie cookie, QuestionDTO questionDTO) throws IOException {
        Result<String> result = new Result<>();
        String message = "";
        if(null!=cookie){
            Integer userId=LoginCacheUtil.loginUser.get(cookie.getValue());
            OjUser user=ojUserMapper.selectByPrimaryKey(userId);
            if(user.getRole().equals("teacher")){
                if (isQuestionDTOEmpty(questionDTO)){
                    message = "Incomplete question information";
                    log.error(message);
                } else if (questionDTO.getTitle().length() < 2 ||questionDTO.getTitle().length() > 25) {
                    message = "The title should be between 2 and 25 characters! " + questionDTO.getTitle();
                    log.error(message);
                } else if (questionDTO.getDescription().length() < 20 || questionDTO.getDescription().length() > 100 ) {
                    message = "description should be between 20 and 100 characters!";
                    log.error(message);
                } else if (!isValidUrl(questionDTO.getPicture_url())) {
                    message = "picture_url is invalid !" + questionDTO.getPicture_url();
                    log.error(message);
                } else if (questionDTO.getCategory().length() > 12) {
                    message = "Category cannot exceed 12 characters! " + questionDTO.getCategory();
                    log.error(message);
                }
                if (message.isEmpty()) {
                    OjQuestion ojQuestion = new OjQuestion();
                    BeanUtils.copyProperties(questionDTO, ojQuestion);
                    try {
                        questionDTO.setModifier(user.getUsername());
                        ojQuestionMapper.insertSelective(ojQuestion);
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
            }
            else {
                message = "Permission denied!";
            }
            result.setMessage(message);
        }
        else {
            message = "Cannot Get User Info!";
            result.setMessage(message);
        }
        return result;
    }

    @Override
    public boolean isQuestionDTOEmpty(QuestionDTO questionDTO) {
        return Objects.isNull(questionDTO)
                || ObjectUtils.isEmpty(questionDTO.getTitle())
                || ObjectUtils.isEmpty(questionDTO.getDescription())
                || ObjectUtils.isEmpty(questionDTO.isStatus())
                || ObjectUtils.isEmpty(questionDTO.getPicture_url())
                || ObjectUtils.isEmpty(questionDTO.getCategory());
    }

    @Override
    public boolean isValidUrl(String url) throws IOException {
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
//            File file = new File(url);
//            BufferedImage image = ImageIO.read(file);
//            if (Objects.isNull(image)){
//                return true;
//            }
//            return false;
            return true;
        }
        return false;
    }
}
