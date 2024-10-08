package com.sideproject.withpt.application.trainer.service.dto.complex;

import static com.sideproject.withpt.domain.trainer.Trainer.getProfileImageUrlBySex;

import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import com.sideproject.withpt.common.type.LoginType;
import com.sideproject.withpt.common.type.OAuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSignUpDto {

    private String email;
    private String name;
    private LocalDate birth;
    private Sex sex;
    private OAuthProvider oauthProvider;
    private List<CareerDto> careers;
    private List<AcademicDto> academics;
    private List<CertificateDto> certificates;
    private List<AwardDto> awards;
    private List<EducationDto> educations;
    private List<GymScheduleDto> gyms;

    @Builder
    public TrainerSignUpDto(String email, String name, LocalDate birth, Sex sex, OAuthProvider oauthProvider, List<CareerDto> careers,
        List<AcademicDto> academics, List<CertificateDto> certificates, List<AwardDto> awards, List<EducationDto> educations,
        List<GymScheduleDto> gyms) {
        this.email = email;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
        this.oauthProvider = oauthProvider;
        this.careers = careers;
        this.academics = academics;
        this.certificates = certificates;
        this.awards = awards;
        this.educations = educations;
        this.gyms = gyms;
    }

    public Trainer toTrainerEntity() {

        Trainer trainer = Trainer.signUpBuilder()
            .email(this.email)
            .name(this.name)
            .birth(this.birth)
            .imageUrl(getProfileImageUrlBySex(this.sex))
            .sex(this.sex)
            .loginType(LoginType.SOCIAL)
            .oauthProvider(this.oauthProvider)
            .role(Role.TRAINER)
            .joinDate(LocalDateTime.now())
            .build();

        addCareerEntities(trainer);
        addAcademicEntities(trainer);
        addCertificateEntities(trainer);
        addAwardEntities(trainer);
        addEducationEntities(trainer);

        return trainer;
    }

    private void addCareerEntities(Trainer trainer) {
        careers.forEach(careerDto -> trainer.addCareer(careerDto.toEntity()));
    }

    private void addAcademicEntities(Trainer trainer) {
        academics.forEach(academicDto -> trainer.addAcademic(academicDto.toEntity()));
    }

    private void addCertificateEntities(Trainer trainer) {
        certificates.forEach(certificateDto -> trainer.addCertificate(certificateDto.toEntity()));
    }

    private void addAwardEntities(Trainer trainer) {
        awards.forEach(awardDto -> trainer.addAward(awardDto.toEntity()));
    }

    private void addEducationEntities(Trainer trainer) {
        educations.forEach(educationDto -> trainer.addEducation(educationDto.toEntity()));
    }

}
