package com.sideproject.withpt.domain.user.member;

import com.sideproject.withpt.common.type.AuthProvider;
import com.sideproject.withpt.common.type.DietType;
import com.sideproject.withpt.common.type.ExerciseFrequency;
import com.sideproject.withpt.common.type.Role;
import com.sideproject.withpt.common.type.Sex;
import com.sideproject.withpt.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class Member extends User {

    private String nickname;

    private Double height;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private DietType dietType;

    @Enumerated(EnumType.STRING)
    private ExerciseFrequency exerciseFrequency;

    private Double targetWeight;

    @Builder
    public Member(String email, String password, String name, String imageUrl, LocalDate birth, Role role, Sex sex, AuthProvider authProvider, LocalDateTime joinDate, String nickname, Double height, Double weight, DietType dietType, ExerciseFrequency exerciseFrequency, Double targetWeight) {
        super(email, password, name, imageUrl, birth, role, sex, authProvider, joinDate);
        this.nickname = nickname;
        this.height = height;
        this.weight = weight;
        this.dietType = dietType;
        this.exerciseFrequency = exerciseFrequency;
        this.targetWeight = targetWeight;
    }

    public void changeCurrentWeight(double weight) {
        this.weight = weight;
    }

    public void editMemberInfo(String name, LocalDate birth, Sex sex, Double height, Double weight) {
        this.setName(name);
        this.setBirth(birth);
        this.setSex(sex);
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
