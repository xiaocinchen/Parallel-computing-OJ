package com.offer.oj;

import com.offer.oj.dao.Result;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.JetcacheExample;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OjApplicationTests {

    @Autowired
    private JetcacheExample jetcacheExample;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

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

    @Test
    void testRegister() throws InterruptedException {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("xiao");
        userDTO.setLastName("spade");
        userDTO.setGender("male");
        userDTO.setPassword("1232123123");
        userDTO.setUsername("ll20111");
        userDTO.setEmail("spadexi6@gmail.com");
        Result<String> result = userService.registerSendEmail(userDTO);
        System.out.println(result);
        if (result.isSuccess()) {
            Thread.sleep(2000);
            userDTO.setUsername("xxx");
            testSendEmail(userDTO);
        }
        else{
            return;
        }
    }

    @Test
    void testSendEmail() throws InterruptedException {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("provider7");
        userDTO.setEmail("spadexiao6@gmail.com");
        emailService.sendRegisterVerifyEmail(userDTO);
        Thread.sleep(3000);
        VerificationDTO verificationDTO = new VerificationDTO();
        verificationDTO.setCode("2017");
        verificationDTO.setUsername("provider7");
        verificationDTO.setType("REGISTER");
        userService.registerVerifyEmail(verificationDTO);
    }

    void testSendEmail(UserDTO userDTO) throws InterruptedException {
        emailService.sendRegisterVerifyEmail(userDTO);
        Thread.sleep(2000);
        VerificationDTO verificationDTO = new VerificationDTO();
        verificationDTO.setCode("2017");
        verificationDTO.setUsername(userDTO.getUsername());
        verificationDTO.setType("REGISTER");
        userService.registerVerifyEmail(verificationDTO);
    }

    @Test
    void testEnumClass() {
        String a = "REGISTER";
        System.out.println(a.equals(EmailTypeEnum.REGISTER.getValue()));
    }
}
