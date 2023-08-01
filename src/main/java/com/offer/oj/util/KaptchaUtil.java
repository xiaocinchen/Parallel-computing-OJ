package com.offer.oj.util;

import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.offer.oj.domain.dto.KaptchaDTO;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.util.Properties;

@Slf4j
public class KaptchaUtil {

    private static DefaultKaptcha defaultKaptcha = null;

    static {
        // Configure the producer
        Properties properties = new Properties();
        properties.setProperty(Constants.KAPTCHA_BORDER, "no");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");

        Config config = new Config(properties);
        defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
    }

    public static KaptchaDTO getKaptcha() {
        String kaptchaText = defaultKaptcha.createText();
        BufferedImage kaptchaImage = defaultKaptcha.createImage(kaptchaText);
        return new KaptchaDTO(kaptchaText, kaptchaImage);
    }

}
