package com.offer.oj;

import com.offer.oj.MQ.sender.CodeMQSender;
import com.offer.oj.dao.QuestionMapper;
import com.offer.oj.dao.mapper.OjCodeMapper;
import com.offer.oj.dao.mapper.OjQuestionMapper;
import com.offer.oj.domain.OjCode;
import com.offer.oj.domain.dto.*;
import com.offer.oj.domain.OjUser;
import com.offer.oj.domain.dto.QuestionDTO;
import com.offer.oj.domain.enums.CodeTypeEnum;
import com.offer.oj.domain.enums.EmailTypeEnum;
import com.offer.oj.domain.query.CodeResultListQuery;
import com.offer.oj.domain.query.QuestionInnerQuery;
import com.offer.oj.service.CodeService;
import com.offer.oj.service.EmailService;
import com.offer.oj.service.JetcacheExample;
import com.offer.oj.service.KaptchaService;
import com.offer.oj.service.QuestionService;
import com.offer.oj.service.UserService;
import com.offer.oj.util.pack.DockerUtil;
import com.offer.oj.util.EncryptionUtil;
import com.offer.oj.util.TimeUtil;
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

    @Autowired
    private DockerUtil dockerUtil;

    @Autowired
    private CodeMQSender codeMQSender;

    @Autowired
    private OjCodeMapper ojCodeMapper;

    @Autowired
    private CodeService codeService;

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
        String hash = EncryptionUtil.hashPassword("123456");
        System.out.println(hash);
        System.out.println(EncryptionUtil.checkPassword(password, hash));
        System.out.println(EncryptionUtil.checkPassword("password", hash));
    }

//    @Test
//    void testRegister() throws InterruptedException, IOException {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setFirstName("xiao");
//        userDTO.setLastName("spade");
//        userDTO.setGender("male");
//        userDTO.setPassword("1232123123");
//        userDTO.setUsername("ll20111");
//        userDTO.setEmail("spadexi6@gmail.com");
//        Result<String> result = userService.registerSendEmail(userDTO);
//        System.out.println(result);
//        if (result.isSuccess()) {
//            Thread.sleep(2000);
//            userDTO.setUsername("xxx");
//            testSendEmail(userDTO);
//        }
//        else{
//            return;
//        }
//    }

//    @Test
//    void testSendEmail() throws InterruptedException, IOException {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername("provider7");
//        userDTO.setEmail("spadexiao6@gmail.com");
//
//        emailService.sendRegisterVerifyEmail(userDTO, );
//        Thread.sleep(3000);
//        VerificationDTO verificationDTO = new VerificationDTO();
//        verificationDTO.setCode("2017");
//        verificationDTO.setUsername("provider7");
//        verificationDTO.setType("REGISTER");
//        userService.registerVerifyEmail(verificationDTO);
//    }

//    void testSendEmail(UserDTO userDTO) throws InterruptedException, IOException {
//        emailService.sendRegisterVerifyEmail(userDTO, );
//        Thread.sleep(2000);
//        VerificationDTO verificationDTO = new VerificationDTO();
//        verificationDTO.setCode("2017");
//        verificationDTO.setUsername(userDTO.getUsername());
//        verificationDTO.setType("REGISTER");
//        userService.registerVerifyEmail(verificationDTO);
//    }

    @Test
    void testEnumClass() {
        String a = "REGISTER";
        System.out.println(a.equals(EmailTypeEnum.REGISTER.getValue()));
    }

//    @Test
//    void testKaptcha() throws IOException {
//        String username = "MAJ";
//        String code = kaptchaService.getKaptcha().getData().getCode();
//        kaptchaService.checkKaptcha(code);
//    }
    @Test
    void testForgetPassword() throws IOException {
        ForgetPasswordDTO user = new ForgetPasswordDTO();
        user.setUsername("dave");
        user.setEmail("18811776339@163.com");
        userService.forgetPassword(user);
    }
    @Test
    void testOjQuestionMapper(){
        QuestionInnerQuery questionInnerQuery = new QuestionInnerQuery();
        questionInnerQuery.setId(1);
        System.out.println(ojQuestionMapper.queryForList(questionInnerQuery));
        System.out.println(ojQuestionMapper.queryForCount(questionInnerQuery));
    }

//    @Test
//    void testAddQuestion() throws IOException {
//        OjUser user = new OjUser();
//        user.setRole("teacher");
//        user.setUsername("dave");
//        VariableQuestionDTO question = new VariableQuestionDTO();
//        question.setModifier(user.getUsername());
//        question.setTitle("解数独");
//        question.setDescription("编写一个程序，通过填充空格来解决数独问题。数独的解法需 遵循如下规则：" +
//                "数字1-9在每一行只能出现一次。数字1-9在每一列只能出现一次。数字1-9在每一个以粗实线分隔的3x3宫内只能出现一次。（请参考示例图）数独部分空格内已填入了数字，空白格用'.'表示。" );
//        question.setPictureUrl("https://assets.leetcode-cn.com/aliyun-lc-upload/uploads/2021/04/12/250px-sudoku-by-l2g-20050714svg.png");
//        questionService.addQuestion(question);
//        questionService.addQuestion(question);
//    }
    @Test
    void testSelectQuestion(){
//        String title1 = "解数独";
//        System.out.println(questionService.searchQuestion(title1));
        String title = "回文数";
//        System.out.println(questionService.searchQuestion(title));
//        String title2 = "";
//        System.out.println(questionService.searchQuestion(title2));


//        System.out.println(questionService.queryQuestionsByTitle(title, 1, 1));
    }

//    @Test
//    void testModifyQuestion(){
//        VariableQuestionDTO questionDTO = new VariableQuestionDTO();
//        questionDTO.setId(19);
//        questionDTO.setModifier("php");
//        questionDTO.setDescription("给定一个整数数组 nums和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。\" +\n" +
//                "                \"你可以假设每种输入只");
//        questionDTO.setTitle("5数独");
//        questionService.modifyQuestion(questionDTO);
//    }


//    @Test
//    void testInsertWrongQuestion(){
//        VariableQuestionDTO question = new VariableQuestionDTO();
//        question.setTitle(null);
//        question.setDescription("给定一个整数数组 nums和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。" +
//                "你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。你可以按任意顺序返回答案。" );
//        question.setPictureUrl("https://pic.leetcode.cn/1684401557-pILmGc-output.lin%20(1).png?x-oss-process=image%2Fformat%2Cwebp");
//        questionMapper.insertSelective(question);
//    }

    @Test
    void deleteQuestion(){
        int id = 7;
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(id);
        questionService.deleteQuestion(questionDTO);
    }

    @Test
    void testDocker() throws IllegalAccessException, InterruptedException, IOException {
        SubmitCodeDTO submitCodeDTO = new SubmitCodeDTO();
        submitCodeDTO.setFileName("1_2_07221226418120016");
        submitCodeDTO.setIsResult(false);
        submitCodeDTO.setAuthorId(2);
        submitCodeDTO.setType(CodeTypeEnum.JAVA);
        System.out.println(dockerUtil.executeCodeAndGetResult(submitCodeDTO));
    }

//    @Test
//    void testJudgeQuestion(){
//        SubmitCodeDTO submitCodeDTO = new SubmitCodeDTO();
//        submitCodeDTO.setFileName("main");
//        submitCodeDTO.setContent("""
//                #include <iostream>
//
//                int main() {
//                    std::cout << "Hello, World!" << std::endl;
//                    return 0;
//                }
//                """);
//        submitCodeDTO.setType(CodeTypeEnum.C_PLUS_PLUS);
//        codeMQSender.sendCodeForJudgeMQ(submitCodeDTO);
//    }

    @Test
    void testTimeUtil(){
        System.out.println(TimeUtil.getUniqueSequence());
    }

    @Test
    void testModifyPassword(){
        ModifyPasswordDTO modifyPasswordDTO = new ModifyPasswordDTO();
        modifyPasswordDTO.setUsername("dave");
        modifyPasswordDTO.setPassword("123456");
        modifyPasswordDTO.setKaptchaCode("5000");
        userService.modifyPassword(modifyPasswordDTO);
    }

    @Test
    void testUpdateByFileName(){
        OjCode ojCode = new OjCode();
        ojCode.setFileName("1_32_07182214262830000");
        ojCode.setAcNumber(1);
        ojCode.setTestNumber(20);
        ojCode.setExecutionMemory(128);
        ojCodeMapper.updateByFileName(ojCode);
    }

    @Test
    void TestGetCodeResult(){
        CodeResultListQuery codeResultListQuery = new CodeResultListQuery();
        codeResultListQuery.setPageSize(1);
        codeResultListQuery.setPage(0);
        codeResultListQuery.setAuthorId(32);
        System.out.println(codeService.getCodeResult(codeResultListQuery));
    }

    @Test
    void testQuestionDetail(){
        System.out.println(questionService.QuestionDetail(19));
    }


    @Test
    void TestGetCodeResult(){
        CodeResultListQuery codeResultListQuery = new CodeResultListQuery();
        codeResultListQuery.setPageSize(1);
        codeResultListQuery.setPage(0);
        codeResultListQuery.setAuthorId(32);
        System.out.println(codeService.getCodeResult(codeResultListQuery));
    }

    @Test
    void testQuestionDetail(){
        System.out.println(questionService.QuestionDetail(19));
    }
}
