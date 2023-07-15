package com.offer.oj.service;
import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.KaptchaDTO;
import com.offer.oj.domain.dto.UserDTO;

import java.io.IOException;

public interface KaptchaService {

    Result<KaptchaDTO> getKaptcha(UserDTO userDTO) throws IOException;

    void saveKaptchaCode(KaptchaDTO kaptchaDTO, UserDTO userDTO);

    Result checkKaptcha(String code);

}
