package com.sideproject.withpt.application.member.dto.request;

import com.sideproject.withpt.application.type.DietType;
import com.sideproject.withpt.application.type.LoginType;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.member.Authentication;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.member.SocialLogin;
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

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    private LocalDate birth;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Sex.class)
    private Sex sex;

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

    public Member toMemberEntity() {
        return Member.builder()
            .email(this.email)
            .name(this.name)
            .nickname(this.nickname)
            .height(this.height)
            .weight(this.weight)
            .imageUrl(null)
            .dietType(this.dietType)
            .targetExerciseTimes(this.targetExerciseTimes)
            .targetWeight(this.targetWeight)
            .role(Role.MEMBER)
            .authentication(toAuthenticationEntity())
            .socialLogin(toSocialLoginEntity())
            .build();
    }

    private Authentication toAuthenticationEntity() {
        return Authentication.builder()
            .birth(this.birth)
            .sex(this.sex)
            .loginType(LoginType.SOCIAL)
            .joinDate(LocalDateTime.now())
            .build();
    }

    private SocialLogin toSocialLoginEntity() {
        return SocialLogin.builder()
            .oauthProvider(this.oauthProvider)
            .build();
    }
}
