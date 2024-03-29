package com.offer.oj.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class KaptchaDTO implements Serializable {
    private String code;

    private BufferedImage image;

}
