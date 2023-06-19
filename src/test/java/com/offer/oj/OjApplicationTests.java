package com.offer.oj;

import com.offer.oj.util.Encryption;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OjApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test()
    void testEncryption() {
        String password = "123456";
        String hash = Encryption.hashPassword("123456");
        System.out.println(hash);
        System.out.println(Encryption.checkPassword(password, hash));
        System.out.println(Encryption.checkPassword("password", hash));
    }
}
