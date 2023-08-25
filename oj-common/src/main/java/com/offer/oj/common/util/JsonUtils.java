package com.offer.oj.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class JsonUtils {


    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJsonString(T obj) {
        if (obj != null) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("将对象转换string形式出现异常", e);
            }
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isNotBlank(json) && clazz != null) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                log.info("将json形式的字符串转换为对象出现异常,json内容：" + json);
            }
        }
        return null;
    }

    public static <T> byte[] toJsonBytes(T obj) {
        if (obj != null) {
            try {
                return objectMapper.writeValueAsBytes(obj);
            } catch (IOException e) {
                log.error("将对象转换json形式的字节数组出现异常", e);
            }
        }
        return null;
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        if (bytes != null && clazz != null) {
            try {
                return objectMapper.readValue(bytes, clazz);
            } catch (IOException e) {
                log.error("将json形式的字节转换为对象出现异常", e);
            }
        }
        return null;
    }

    public static byte[] imageToJsonBytes(RenderedImage image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static BufferedImage imageFromJson(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
    }

}
