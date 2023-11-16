package com.sideproject.withpt.common.exception.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.sideproject.withpt.application.member.dto.request.MemberSignUpRequest;
import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.OAuthProvider;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnumValidatorTest {

    private static Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void GenderTypeNotValid() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("test@nave.com")
            .name("test")
            .sex(null) // gender에는 null이 들어갈 수 없음
            .dietType(DietType.BULK_UP)
            .oauthProvider(OAuthProvider.KAKAO)
            .build();

        //when
        Set<ConstraintViolation<MemberSignUpRequest>> validate = validator.validate(request);

        //then
        assertThat(validate.size()).isEqualTo(1); // gender 만 위반하여 1건
        assertThat(validate).extracting("message")
            .containsOnly("해당 필드의 타입에서 지원하지 않는 값입니다. MAN|WOMAN");
    }
}