package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.LoginType;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import io.jsonwebtoken.lang.Assert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String imageUrl;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime joinDate;

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

    @Builder(builderClassName = "BySignUpBuilder", builderMethodName = "BySignUpBuilder")
    public Trainer(String email, String password, String name, String imageUrl, LocalDate birth, Sex sex,
        LoginType loginType, AuthProvider authProvider, Role role, LocalDateTime joinDate) {
        Assert.notNull(email, "email must not be null");
        Assert.notNull(imageUrl, "imageUrl must not be null");
        Assert.notNull(birth, "birth must not be null");
        Assert.notNull(sex, "sex must not be null");
        Assert.notNull(loginType, "loginType must not be null");
        Assert.notNull(authProvider, "oauthProvider must not be null");
        Assert.notNull(role, "role must not be null");

        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
        this.authProvider = authProvider;
        this.role = role;
        this.joinDate = joinDate;
        this.careers = new ArrayList<>();
        this.academics = new ArrayList<>();
        this.certificates = new ArrayList<>();
        this.awards = new ArrayList<>();
        this.educations = new ArrayList<>();
    }

    @Builder(builderClassName = "signUpBuilder", builderMethodName = "signUpBuilder")
    public Trainer(Long id, String email, String password, String name, String imageUrl, LocalDate birth, Sex sex, AuthProvider authProvider,
        Role role, LocalDateTime joinDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
        this.authProvider = authProvider;
        this.role = role;
        this.joinDate = joinDate;
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
        this.imageUrl = imageUrl;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
    }
}
