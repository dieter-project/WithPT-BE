package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.dto.complex.TrainerSignUpDto;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
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

    @ValidEnum(regexp = "KAKAO|GOOGLE", enumClass = OAuthProvider.class)
    private OAuthProvider oauthProvider;

    @Builder.Default
    private List<CareerRequest> careers = new ArrayList<>();

    @Builder.Default
    private List<AcademicRequest> academics = new ArrayList<>();

    @Builder.Default
    private List<CertificateRequest> certificates = new ArrayList<>();

    @Builder.Default
    private List<AwardRequest> awards = new ArrayList<>();

    @Builder.Default
    private List<EducationRequest> educations = new ArrayList<>();

    @Builder.Default
    private List<TrainerGymScheduleRequest> gyms = new ArrayList<>();
    
    public TrainerSignUpDto toServiceTrainerSignUpDto() {
        return TrainerSignUpDto.builder()
            .email(this.email)
            .name(this.name)
            .birth(this.birth)
            .sex(this.sex)
            .oauthProvider(this.oauthProvider)
            .careers(CareerRequest.toCareerDtos(careers))
            .academics(AcademicRequest.toAcademicDtos(academics))
            .certificates(CertificateRequest.toCertificateDtos(certificates))
            .awards(AwardRequest.toAwardDtos(awards))
            .educations(EducationRequest.toEducationDtos(educations))
            .gyms(TrainerGymScheduleRequest.toTrainerGymScheduleDtos(gyms))
            .build();
    }


}
