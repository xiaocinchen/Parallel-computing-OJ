package com.offer.oj.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtil {

    /**
     * 成本因子
     */
    private static final int WORKLOAD = 12;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(WORKLOAD);

    private EncryptionUtil(){}

    /**
     * 将密码哈希
     */
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    /**
     * 验证密码
     */
    public static boolean checkPassword(String password, String hash) {
        return encoder.matches(password, hash);
    }

}
