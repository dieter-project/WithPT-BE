package com.sideproject.withpt.application.trainer.service.dto.complex;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.constraints.Size.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;

@Import(TrainerSignUpDtoTest.TestProvider.class)
class TrainerSignUpDtoTest {

//    @Value("${default.profile.url}")
//    private String url;

    @Autowired
    TestProvider provider;

    @Test
    public void test() {
        //given
        System.out.println(provider.image);
        //when
        //then
    }


    static class TestProvider {
        @Value("${default.profile.url}")
        String image;

    }
}