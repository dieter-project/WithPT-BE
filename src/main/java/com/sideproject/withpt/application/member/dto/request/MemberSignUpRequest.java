package com.sideproject.withpt.application.member.dto.request;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.Gender;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.Member;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    private LocalDate birth;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Gender.class)
    private Gender gender;

    @NotBlank(message = "닉네임은 필수 입력입니다.")
    private String nickname;

    private Double height;

    private Double weight;

    @ValidEnum(enumClass = DietType.class)
    private DietType dietType;

    private int targetExerciseTimes;

    private Double targetWeight;

    @ValidEnum(regexp = "KAKAO|GOOGLE", enumClass = OAuthProvider.class)
    private OAuthProvider oauthProvider;

    public Member toEntity() {
        return Member.builder()
            .email(this.email)
            .name(this.name)
            .birth(this.birth)
            .gender(this.gender)
            .nickname(this.nickname)
            .height(this.height)
            .weight(this.weight)
            .dietType(this.dietType)
            .targetExerciseTimes(this.targetExerciseTimes)
            .targetWeight(this.targetWeight)
            .oauthProvider(this.oauthProvider)
            .role(Role.USER)
            .build();
    }
}
