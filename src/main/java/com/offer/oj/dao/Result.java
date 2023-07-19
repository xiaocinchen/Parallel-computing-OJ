package com.offer.oj.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 通用结果类
 * @Date: 2023/6/19 00:33
 * @Author: Simuel
 */
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    private boolean success;

    private Integer code;

    private String message;

    private T data;

    /**
     * 简单构造函数
     */
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public void setSimpleResult(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    public void setSimpleResult(boolean success, Integer code){
        this.success = success;
        this.code = code;
    }

    public void setSimpleResult(boolean success, String message, Integer code){
        this.success = success;
        this.message = message;
        this.code = code;
    }

}
