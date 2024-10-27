package com.sideproject.withpt.application.trainer.controller.request;

import com.sideproject.withpt.application.trainer.service.model.complex.TrainerSignUpDto;
import com.sideproject.withpt.common.exception.validator.ValidEnum;
import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Sex;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSignUpRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String password;

    @NotBlank(message = "이름은 필수 입력입니다.")
    private String name;

    private LocalDate birth;

    @ValidEnum(regexp = "MAN|WOMAN", enumClass = Sex.class)
    private Sex sex;

    @ValidEnum(regexp = "KAKAO|GOOGLE|EMAIL", enumClass = AuthProvider.class)
    private AuthProvider authProvider;

    @Valid
    private List<CareerRequest> careers = new ArrayList<>();

    @Valid
    private List<AcademicRequest> academics = new ArrayList<>();

    @Valid
    private List<CertificateRequest> certificates = new ArrayList<>();

    @Valid
    private List<AwardRequest> awards = new ArrayList<>();

    @Valid
    private List<EducationRequest> educations = new ArrayList<>();

    @Valid
    private List<TrainerGymScheduleRequest> gyms = new ArrayList<>();

    public TrainerSignUpDto toServiceTrainerSignUpDto() {
        return TrainerSignUpDto.builder()
            .email(this.email)
            .password(this.password)
            .name(this.name)
            .birth(this.birth)
            .sex(this.sex)
            .authProvider(this.authProvider)
            .careers(CareerRequest.toCareerDtos(careers))
            .academics(AcademicRequest.toAcademicDtos(academics))
            .certificates(CertificateRequest.toCertificateDtos(certificates))
            .awards(AwardRequest.toAwardDtos(awards))
            .educations(EducationRequest.toEducationDtos(educations))
            .gyms(TrainerGymScheduleRequest.toTrainerGymScheduleDtos(gyms))
            .build();
    }


}
