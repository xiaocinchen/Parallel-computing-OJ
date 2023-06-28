package com.offer.oj.dao.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OjUserMapperTests {
    @Autowired
    private OjUserMapper ojUserMapper;

    @Test
    void selectByUsernameTest(){
//        System.out.println(ojUserMapper.selectByUsername("Charlie"));
        System.out.println(ojUserMapper.selectByUsername("Charlie1"));
    }
}
