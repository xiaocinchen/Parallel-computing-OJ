package com.offer.oj;

import com.offer.oj.service.JetcacheExample;
import com.offer.oj.util.Encryption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OjApplicationTests {

    @Autowired
    private JetcacheExample jetcacheExample;

    @Test
    void contextLoads() {
    }


    @Test
    void jedisTest() {
        System.out.println(jetcacheExample.getUserById(132L));
        jetcacheExample.updateUser(132L, "asdl");
        System.out.println(jetcacheExample.getUserById(132L));
        jetcacheExample.deleteUser(132L);
        System.out.println(jetcacheExample.getUserById(132L));
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
