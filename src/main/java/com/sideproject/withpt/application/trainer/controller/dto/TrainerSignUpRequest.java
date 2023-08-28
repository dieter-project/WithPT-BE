package com.sideproject.withpt.application.trainer.controller.dto;

import com.sideproject.withpt.application.trainer.service.TrainerService;
import com.sideproject.withpt.application.type.LoginType;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.domain.trainer.Career;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class TrainerSignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    private LocalDate birth;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Sex.class)
    private Sex sex;

    @NotBlank(message = "닉네임은 필수 입력입니다.")
    private String nickname;

    @ValidEnum(regexp = "KAKAO|GOOGLE", enumClass = OAuthProvider.class)
    private OAuthProvider oauthProvider;

    private List<CareerDto> careers = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CareerDto {

        private String centerName;
        private LocalDate yearOfService;

        public Career toEntity() {
            return Career.builder()
                .centerName(this.centerName)
                .yearOfService(this.yearOfService)
                .build();
        }
    }

    public Trainer toEntity() {
        return Trainer.builder()
            .email(this.email)
            .nickname(this.nickname)
            .name(this.name)
            .imageUrl(null)
            .birth(this.birth)
            .sex(this.sex)
            .loginType(LoginType.SOCIAL)
            .careers(new ArrayList<>())
            .role(Role.TRAINER)
            .oauthProvider(this.oauthProvider)
            .joinDate(LocalDateTime.now())
            .build();
    }
}
