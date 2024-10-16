package com.sideproject.withpt.domain.member;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.BaseEntity;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private Double height;

    private Double weight;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private DietType dietType;

    @Enumerated(EnumType.STRING)
    private ExerciseFrequency exerciseFrequency;

    private Double targetWeight;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "authentication_id")
    private Authentication authentication;


    public void changeCurrentWeight(double weight) {
        this.weight = weight;
    }

    public void addDefaultImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void editMemberInfo(String name, LocalDate birth, Sex sex, Double height, Double weight) {
        this.name = name;
        this.authentication.editMemberInfo(birth, sex);
        this.height = height;
        this.weight = weight;
    }

    public void editDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public void editExerciseFrequency(ExerciseFrequency exerciseFrequency) {
        this.exerciseFrequency = exerciseFrequency;
    }

    public void editTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }
}
