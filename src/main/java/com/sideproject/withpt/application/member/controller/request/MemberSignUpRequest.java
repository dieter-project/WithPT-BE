package com.sideproject.withpt.application.member.controller.request;

import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.user.member.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String password;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    private LocalDate birth;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Sex.class)
    private Sex sex;

    private Double height;

    private Double weight;

    @ValidEnum(enumClass = DietType.class)
    private DietType dietType;

    private Double targetWeight;

    @ValidEnum(enumClass = ExerciseFrequency.class)
    private ExerciseFrequency exerciseFrequency;

    @ValidEnum(regexp = "KAKAO|GOOGLE|EMAIL", enumClass = AuthProvider.class)
    private AuthProvider authProvider;

    public Member toMemberEntity() {
        return Member.builder()
            .email(this.email)
            .password(this.password)
            .name(this.name)
            .height(this.height)
            .weight(this.weight)
            .dietType(this.dietType)
            .targetWeight(this.targetWeight)
            .exerciseFrequency(this.exerciseFrequency)
            .role(Role.MEMBER)
            .authProvider(authProvider)
            .birth(this.birth)
            .sex(this.sex)
            .joinDate(LocalDateTime.now())
//            .authentication(toAuthenticationEntity())
            .build();
    }

//    private Authentication toAuthenticationEntity() {
//        return Authentication.builder()
//            .birth(this.birth)
//            .sex(this.sex)
//            .loginType(LoginType.SOCIAL)
//            .joinDate(LocalDateTime.now())
//            .build();
//    }
}
