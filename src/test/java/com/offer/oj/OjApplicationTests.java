package com.offer.oj;

import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.Result;
import com.offer.oj.dao.UserMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.dto.UserDTO;
import com.offer.oj.domain.dto.VerificationDTO;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.domain.query.QuestionInnerQuery;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.JetcacheExample;
import com.offer.oj.service.KaptchaService;
import com.offer.oj.service.QuestionService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.Encryption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class OjApplicationTests {

    @Autowired
    private JetcacheExample jetcacheExample;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KaptchaService kaptchaService;

    @Autowired
    private OjQuestionMapper ojQuestionMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

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

    @Test
    void testKaptcha() throws IOException {
        UserDTO userDTO = new UserDTO();
        String code = kaptchaService.getKaptchaImage(userDTO).getData().getCode();
        kaptchaService.checkKaptcha(code);
    }
    @Test
    void testForgetPassword(){
        ForgetPasswordDTO user = new ForgetPasswordDTO();
        user.setUsername("ll20111");
        user.setEmail("spade6@gmail.com");
        userService.forgetPassword(user);
    }
    @Test
    void testOjQuestionMapper(){
        QuestionInnerQuery questionInnerQuery = new QuestionInnerQuery();
        questionInnerQuery.setId(1);
        System.out.println(ojQuestionMapper.queryForList(questionInnerQuery));
        System.out.println(ojQuestionMapper.queryForCount(questionInnerQuery));
    }

    @Test
    void testAddQuestion() throws IOException {
        OjUser user = new OjUser();
        user.setRole("teacher");
        user.setUsername("MAJ");
        VariableQuestionDTO question = new VariableQuestionDTO();
        question.setModifier(user.getUsername());
        question.setTitle("两数之和");
        question.setDescription("给定一个整数数组 nums和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。" +
                "你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。你可以按任意顺序返回答案。" );
        question.setPictureUrl("https://pic.leetcode.cn/1684401557-pILmGc-output.lin%20(1).png?x-oss-process=image%2Fformat%2Cwebp");
        questionService.addQuestion(question);
    }
    @Test
    void testSelectQuestion(){
        String title1 = "两数之和";
        System.out.println(questionService.selectQuestion(title1));
        String title = "和";
        System.out.println(questionService.selectQuestion(title));
    }
    @Test
    void testInsertWrongQuestion(){
        VariableQuestionDTO question = new VariableQuestionDTO();
        question.setTitle(null);
        question.setDescription("给定一个整数数组 nums和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。" +
                "你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。你可以按任意顺序返回答案。" );
        question.setPictureUrl("https://pic.leetcode.cn/1684401557-pILmGc-output.lin%20(1).png?x-oss-process=image%2Fformat%2Cwebp");
        questionMapper.insertSelective(question);
    }

    @Test
    void deleteQuestion(){
        int id = 9;
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(id);
        questionService.deleteQuestion(questionDTO);
    }
}
