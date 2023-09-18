package com.sideproject.withpt.domain.trainer;

import com.sideproject.withpt.application.type.LoginType;
import com.sideproject.withpt.application.type.OAuthProvider;
import com.sideproject.withpt.application.type.Role;
import com.sideproject.withpt.application.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import com.sideproject.withpt.domain.gym.GymTrainer;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String imageUrl;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime joinDate;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GymTrainer> gymTrainers = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkSchedule> workSchedules = new ArrayList<>();

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
    public Trainer(String email, String name, String imageUrl, LocalDate birth, Sex sex,
        LoginType loginType, OAuthProvider oauthProvider, Role role, LocalDateTime joinDate) {
        Assert.notNull(email, "email must not be null");
        Assert.notNull(imageUrl, "imageUrl must not be null");
        Assert.notNull(birth, "birth must not be null");
        Assert.notNull(sex, "sex must not be null");
        Assert.notNull(loginType, "loginType must not be null");
        Assert.notNull(oauthProvider, "oauthProvider must not be null");
        Assert.notNull(role, "role must not be null");

        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.birth = birth;
        this.sex = sex;
        this.loginType = loginType;
        this.oauthProvider = oauthProvider;
        this.role = role;
        this.joinDate = joinDate;
        this.careers = new ArrayList<>();
        this.academics = new ArrayList<>();
        this.certificates = new ArrayList<>();
        this.awards = new ArrayList<>();
        this.educations = new ArrayList<>();
    }

    // == 연관 관계 메서드 == //
    public void addGymTrainer(GymTrainer gymTrainer) {
        gymTrainer.addTrainer(this);
        this.gymTrainers.add(gymTrainer);
    }

    public void addWorkSchedule(WorkSchedule workSchedule) {
        workSchedule.setTrainer(this);
        this.workSchedules.add(workSchedule);
    }
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


    public static Trainer createSignUpTrainer(Trainer trainer,
        List<WorkSchedule> workSchedules, List<GymTrainer> gymTrainers, List<Career> careers,
        List<Academic> academics, List<Certificate> certificates,
        List<Award> awards, List<Education> educations) {

        workSchedules.forEach(trainer::addWorkSchedule);
        gymTrainers.forEach(trainer::addGymTrainer);
        careers.forEach(trainer::addCareer);
        academics.forEach(trainer::addAcademic);
        certificates.forEach(trainer::addCertificate);
        awards.forEach(trainer::addAward);
        educations.forEach(trainer::addEducation);

        return trainer;
    }
}
