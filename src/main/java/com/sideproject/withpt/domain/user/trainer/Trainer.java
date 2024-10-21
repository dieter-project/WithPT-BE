package com.sideproject.withpt.domain.user.trainer;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TRAINER")
public class Trainer extends User {

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Academic> academics = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    @Builder(builderClassName = "signUpBuilder", builderMethodName = "signUpBuilder")
    public Trainer(String email, String password, String name, String imageUrl, LocalDate birth, Sex sex, AuthProvider authProvider, Role role, LocalDateTime joinDate) {
        super(email, password, name, imageUrl, birth, role, sex, authProvider, joinDate);
    }

    // == 연관 관계 메서드 == //
    public void addCareer(Career career) {
        career.setTrainer(this);
        this.careers.add(career);
    }

    public void addAcademic(Academic academic) {
        academic.setTrainer(this);
        this.academics.add(academic);
    }

    public void addCertificate(Certificate certificate) {
        certificate.setTrainer(this);
        this.certificates.add(certificate);
    }

    public void addAward(Award award) {
        award.setTrainer(this);
        this.awards.add(award);
    }

    public void addEducation(Education education) {
        education.setTrainer(this);
        this.educations.add(education);
    }

    public static String getProfileImageUrlBySex(Sex sex) {
        return sex.equals(Sex.MAN) ?
            "https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/TRAINER_MAN.png" :
            "https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/TRAINER_WOMAN.png";
    }

    public void editTrainerProfile(String imageUrl, String name, LocalDate birth, Sex sex) {
        this.addDefaultImageUrl(imageUrl);
        this.setName(name);
        this.setBirth(birth);
        this.setSex(sex);
    }
}
