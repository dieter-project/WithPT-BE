package com.sideproject.withpt.application.trainer.service.dto.complex;

import com.sideproject.withpt.application.trainer.service.dto.single.AcademicDto;
import com.sideproject.withpt.application.trainer.service.dto.single.AwardDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CareerDto;
import com.sideproject.withpt.application.trainer.service.dto.single.CertificateDto;
import com.sideproject.withpt.application.trainer.service.dto.single.EducationDto;
import com.sideproject.withpt.application.type.LoginType;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.trainer.Trainer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSignUpDto {

    private String email;

    private String name;

    @Value("${default.profile.image.url}")
    private String imageUrl;

    private LocalDate birth;

    private Sex sex;

    private OAuthProvider oauthProvider;

    @Builder.Default
    private List<CareerDto> careers = new ArrayList<>();

    @Builder.Default
    private List<AcademicDto> academics = new ArrayList<>();

    @Builder.Default
    private List<CertificateDto> certificates = new ArrayList<>();

    @Builder.Default
    private List<AwardDto> awards = new ArrayList<>();

    @Builder.Default
    private List<EducationDto> educations = new ArrayList<>();

    @Builder.Default
    private List<TrainerGymScheduleDto> gyms = new ArrayList<>();

    public Trainer toTrainerBasicEntity() {
        return Trainer.BySignUpBuilder()
            .email(this.email)
            .name(this.name)
            .imageUrl(this.imageUrl)
            .birth(this.birth)
            .sex(this.sex)
            .loginType(LoginType.SOCIAL)
            .oauthProvider(this.oauthProvider)
            .role(Role.TRAINER)
            .joinDate(LocalDateTime.now())
            .build();
    }

}
